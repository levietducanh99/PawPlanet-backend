# Google OAuth Login Integration

This document describes how to use the Google OAuth login feature in PawPlanet Backend.

## Overview

The Google OAuth integration allows users to sign in using their Google account instead of creating a password-based account. The backend verifies Google ID tokens, creates or retrieves user accounts, and issues JWT tokens for authentication.

## Configuration

### Environment Variables

Add the following environment variable to your `.env` file:

```env
GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
```

**For Heroku Production:**
```bash
heroku config:set GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
```

### Getting Google Client ID

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Google+ API
4. Go to **Credentials** → **Create Credentials** → **OAuth 2.0 Client ID**
5. Configure the OAuth consent screen
6. Create credentials for:
   - **Web application** (for web login)
   - **Android/iOS** (for mobile login)
7. Copy the Client ID

## API Endpoint

### POST `/api/v1/auth/google`

Authenticates a user with a Google ID token.

#### Request

```json
{
  "idToken": "eyJhbGciOiJSUzI1NiIsImtpZCI6IjU5N..."
}
```

**Headers:**
- `Content-Type: application/json`

#### Success Response (200 OK)

```json
{
  "statusCode": 200,
  "message": "Success",
  "result": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "authenticated": true
  }
}
```

#### Error Response (400 Bad Request)

```json
{
  "statusCode": 401,
  "message": "Invalid or expired Google token"
}
```

## Database Schema

The integration adds the following fields to the `users` table:

| Column | Type | Default | Description |
|--------|------|---------|-------------|
| `auth_provider` | VARCHAR(50) | 'LOCAL' | Authentication provider ('LOCAL' or 'GOOGLE') |
| `provider_user_id` | VARCHAR(255) | NULL | Google user ID (sub claim) |
| `email_verified` | BOOLEAN | FALSE | Whether email is verified |

**Unique Constraint:** `(auth_provider, provider_user_id)` ensures one account per Google user.

## User Flow

### First-time Google User

1. User signs in with Google on frontend
2. Frontend receives Google ID token
3. Frontend sends ID token to `/api/v1/auth/google`
4. Backend verifies token with Google
5. Backend creates new user with:
   - `auth_provider = 'GOOGLE'`
   - `provider_user_id = sub` (Google user ID)
   - `email` from Google
   - `email_verified = true`
   - `full_name` from Google
   - `avatar_url` from Google profile picture
   - Auto-generated unique `username`
6. Backend returns JWT tokens

### Returning Google User

1. User signs in with Google on frontend
2. Frontend sends ID token to `/api/v1/auth/google`
3. Backend verifies token
4. Backend finds existing user by `(auth_provider='GOOGLE', provider_user_id=sub)`
5. Backend returns JWT tokens

## Frontend Integration

### Web (JavaScript/TypeScript)

```typescript
// 1. Load Google Sign-In library
<script src="https://accounts.google.com/gsi/client" async defer></script>

// 2. Initialize Google Sign-In
google.accounts.id.initialize({
  client_id: 'YOUR_GOOGLE_CLIENT_ID',
  callback: handleGoogleLogin
});

// 3. Handle Google login callback
async function handleGoogleLogin(response) {
  try {
    const result = await fetch('/api/v1/auth/google', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        idToken: response.credential
      })
    });
    
    const data = await result.json();
    
    if (data.result.authenticated) {
      // Store tokens
      localStorage.setItem('access_token', data.result.token);
      localStorage.setItem('refresh_token', data.result.refreshToken);
      
      // Redirect to dashboard
      window.location.href = '/dashboard';
    }
  } catch (error) {
    console.error('Login failed:', error);
  }
}

// 4. Render Google Sign-In button
google.accounts.id.renderButton(
  document.getElementById('googleSignInButton'),
  { theme: 'outline', size: 'large' }
);
```

### Mobile (Android)

```kotlin
// 1. Configure Google Sign-In
val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    .requestIdToken(getString(R.string.google_client_id))
    .requestEmail()
    .build()

val googleSignInClient = GoogleSignIn.getClient(this, gso)

// 2. Sign in
val signInIntent = googleSignInClient.signInIntent
startActivityForResult(signInIntent, RC_SIGN_IN)

// 3. Handle result
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == RC_SIGN_IN) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        val account = task.getResult(ApiException::class.java)
        val idToken = account.idToken
        
        // Send to backend
        sendTokenToBackend(idToken)
    }
}

// 4. Send to backend
suspend fun sendTokenToBackend(idToken: String) {
    val response = apiService.loginWithGoogle(GoogleLoginRequest(idToken))
    if (response.result.authenticated) {
        // Save tokens and navigate
    }
}
```

### Mobile (iOS)

```swift
// 1. Configure Google Sign-In
GIDSignIn.sharedInstance.configuration = GIDConfiguration(clientID: "YOUR_CLIENT_ID")

// 2. Sign in
GIDSignIn.sharedInstance.signIn(withPresenting: self) { signInResult, error in
    guard let result = signInResult else { return }
    let idToken = result.user.idToken?.tokenString
    
    // Send to backend
    self.sendTokenToBackend(idToken: idToken)
}

// 3. Send to backend
func sendTokenToBackend(idToken: String?) {
    guard let token = idToken else { return }
    
    let request = GoogleLoginRequest(idToken: token)
    // Make API call to /api/v1/auth/google
}
```

## Security Considerations

1. **Token Verification**: The backend verifies:
   - Token signature using Google's public keys
   - Token audience (aud) matches your Client ID
   - Token is not expired
   - Required claims (sub, email) are present

2. **No Account Linking**: Google accounts and local accounts are separate. A user with the same email but different auth providers will have separate accounts.

3. **Email Verification**: Google users are automatically marked as email verified since Google has already verified their email.

4. **Password for OAuth Users**: OAuth users have a random UUID as password (unusable for login). They can only authenticate via Google.

## Testing

Run the Google OAuth tests:
```bash
mvn test -Dtest=AuthControllerGoogleTest
```

Tests include:
- ✅ Successful login with valid token
- ✅ Error handling for invalid token
- ✅ Validation for missing ID token
- ✅ Validation for empty ID token

## Troubleshooting

### "Invalid or expired Google token"

- Ensure your `GOOGLE_CLIENT_ID` matches the client ID used to generate the token
- Verify the token hasn't expired (tokens are typically valid for 1 hour)
- Check that the token is a valid Google ID token (not an access token)

### "Email already exists"

This shouldn't happen with Google login since we use `(auth_provider, provider_user_id)` as the unique identifier. If it does:
1. Check if a LOCAL user with the same email exists
2. The user should use their local password or reset it
3. Account linking is not currently supported

### Testing Locally

For local development without a real Google token, you can:
1. Mock the `GoogleTokenVerifier` in tests
2. Use a test Google Client ID for development
3. Generate test tokens using Google's OAuth 2.0 Playground

## API Documentation

Full API documentation is available at:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI Spec**: `http://localhost:8080/api-docs`
