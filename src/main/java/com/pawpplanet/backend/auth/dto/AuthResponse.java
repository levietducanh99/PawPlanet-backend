package com.pawpplanet.backend.auth.dto;

import com.pawpplanet.backend.user.dto.UserResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String token;
    private UserResponse user;
}