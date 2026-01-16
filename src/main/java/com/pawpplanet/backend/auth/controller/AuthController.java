package com.pawpplanet.backend.auth.controller;

import com.nimbusds.jose.JOSEException;
import com.pawpplanet.backend.auth.dto.request.*;
import com.pawpplanet.backend.auth.dto.response.AuthResponse;
import com.pawpplanet.backend.auth.dto.response.IntrospectResponse;
import com.pawpplanet.backend.auth.service.AuthService;
import com.pawpplanet.backend.common.dto.ApiResponse;
import com.pawpplanet.backend.user.dto.UserResponse;
import com.pawpplanet.backend.user.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API xác thực người dùng")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Đăng ký tài khoản mới",
            description = "Tạo tài khoản người dùng mới với email, username và password"
    )
    public ApiResponse<UserEntity> register(@RequestBody @Valid RegisterRequest request) throws MessagingException {

        ApiResponse<UserEntity> response = new ApiResponse<>();
                response.setResult(authService.register(request));

                return response;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Đăng nhập",
            description = "Đăng nhập và nhận JWT token để xác thực các request tiếp theo"
    )
    public ApiResponse<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        ApiResponse<AuthResponse> response = new ApiResponse<>();

        response.setResult(
                authService.login(request)
        );
        return response;
    }

    @PostMapping("/google")
    @Operation(
            summary = "Đăng nhập với Google",
            description = "Xác thực Google ID token và tạo/đăng nhập người dùng"
    )
    public ApiResponse<AuthResponse> loginWithGoogle(@RequestBody @Valid GoogleLoginRequest request) {
        ApiResponse<AuthResponse> response = new ApiResponse<>();
        
        response.setResult(
                authService.loginWithGoogle(request.getIdToken())
        );
        return response;
    }

    @PostMapping("/introspect")
    @Operation(
            summary = "Xác thực token",
            description = "Kiểm tra token JWT có hợp lệ và còn hiệu lực không"
    )
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        ApiResponse<IntrospectResponse> response = new ApiResponse<>();
        response.setResult(authService.introspect(request));
        return response;
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        ApiResponse<Void> response = new ApiResponse<>();
        authService.logout(request);
        return response;
    }

    @GetMapping("/{userId}")
    @Operation(
            summary = "Lấy thông tin user theo ID",
            description = "Endpoint này yêu cầu JWT authentication",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<UserResponse> getUserById(@PathVariable("userId") Long userId) {
        ApiResponse<UserResponse> response = new ApiResponse<>();

        response.setResult(
                authService.getUserById(userId)
        );
        return response;
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) throws ParseException, JOSEException {
        ApiResponse<Void> response = new ApiResponse<>();
        authService.changePassword(changePasswordRequest);
        return response;
    }

    @PostMapping("/refresh-token")
    public ApiResponse<AuthResponse> refreshToken(@RequestBody IntrospectRequest refreshTokenRequest) throws  ParseException, JOSEException {
        ApiResponse<AuthResponse> response = new ApiResponse<>();
        response.setResult(
                authService.refreshToken(refreshTokenRequest)
        );
        return response;
    }

    @GetMapping("/verify-email")
    public ApiResponse<?> verifyEmail(@RequestParam("token") String token) throws ParseException {
        ApiResponse<Void> response = new ApiResponse<>();
        authService.verifyEmail(token);
        response.setMessage("Email verified successfully");
        return response;
    }

    @PostMapping("/reset-password")
    public ApiResponse<?> resetPassword(@RequestParam("token") String token, @RequestBody ResetPasswordRequest request) throws ParseException, JOSEException {
        ApiResponse<Void> response = new ApiResponse<>();
        authService.resetPassword(token, request);
        response.setMessage("Password reset successfully");
        return response;
    }

    @PostMapping("/forgot-password")
    public ApiResponse<?> forgotPassword(@RequestBody ForgotPasswordRequest request) throws MessagingException {
        ApiResponse<Void> response = new ApiResponse<>();
        authService.forgotPassword(request);
        response.setMessage("Password reset email sent successfully");
        return response;
    }
}
