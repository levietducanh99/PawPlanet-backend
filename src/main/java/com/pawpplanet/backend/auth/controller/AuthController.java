package com.pawpplanet.backend.auth.controller;

import com.pawpplanet.backend.auth.dto.AuthResponse;
import com.pawpplanet.backend.auth.dto.LoginRequest;
import com.pawpplanet.backend.auth.dto.RegisterRequest;
import com.pawpplanet.backend.auth.service.AuthService;
import com.pawpplanet.backend.common.dto.ApiResponse;
import com.pawpplanet.backend.user.dto.UserResponse;
import com.pawpplanet.backend.user.entity.UserEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<UserEntity> register(@RequestBody @Valid RegisterRequest request) {

        ApiResponse<UserEntity> response = new ApiResponse<>();
                response.setResult(authService.register(request));

                return response;
    }

    @PostMapping("/login")
    public ApiResponse<UserEntity> login(@RequestBody LoginRequest request) {
        ApiResponse<UserEntity> response = new ApiResponse<>();

        response.setResult(
                authService.login(request)
        );

        return response;
    }
}
