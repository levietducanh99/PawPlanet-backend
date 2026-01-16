package com.pawpplanet.backend.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawpplanet.backend.auth.dto.request.GoogleLoginRequest;
import com.pawpplanet.backend.auth.dto.response.AuthResponse;
import com.pawpplanet.backend.auth.service.AuthService;
import com.pawpplanet.backend.common.exception.AppException;
import com.pawpplanet.backend.common.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for Google OAuth authentication endpoint
 */
@WebMvcTest(
    controllers = AuthController.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class
    }
)
class AuthControllerGoogleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void testGoogleLogin_Success() throws Exception {
        // Prepare test data
        GoogleLoginRequest request = new GoogleLoginRequest();
        request.setIdToken("valid-google-id-token");

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken("mock-jwt-access-token");
        authResponse.setRefreshToken("mock-jwt-refresh-token");
        authResponse.setAuthenticated(true);

        // Mock service behavior
        when(authService.loginWithGoogle(anyString())).thenReturn(authResponse);

        // Perform request and verify response
        mockMvc.perform(post("/api/v1/auth/google")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.token").value("mock-jwt-access-token"))
                .andExpect(jsonPath("$.result.refreshToken").value("mock-jwt-refresh-token"))
                .andExpect(jsonPath("$.result.authenticated").value(true));
    }

    @Test
    void testGoogleLogin_InvalidToken() throws Exception {
        // Prepare test data
        GoogleLoginRequest request = new GoogleLoginRequest();
        request.setIdToken("invalid-google-id-token");

        // Mock service behavior to throw exception
        when(authService.loginWithGoogle(anyString()))
                .thenThrow(new AppException(ErrorCode.INVALID_GOOGLE_TOKEN));

        // Perform request and verify error response
        // Note: GlobalExceptionHandler returns 400 for all AppExceptions
        mockMvc.perform(post("/api/v1/auth/google")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.message").value("Invalid or expired Google token"));
    }

    @Test
    void testGoogleLogin_MissingIdToken() throws Exception {
        // Prepare test data with missing idToken
        GoogleLoginRequest request = new GoogleLoginRequest();
        // idToken is null

        // Perform request and verify validation error
        mockMvc.perform(post("/api/v1/auth/google")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGoogleLogin_EmptyIdToken() throws Exception {
        // Prepare test data with empty idToken
        GoogleLoginRequest request = new GoogleLoginRequest();
        request.setIdToken("");

        // Perform request and verify validation error
        mockMvc.perform(post("/api/v1/auth/google")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
