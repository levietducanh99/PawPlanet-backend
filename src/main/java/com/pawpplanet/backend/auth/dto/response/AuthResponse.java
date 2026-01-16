package com.pawpplanet.backend.auth.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String token;
    private String refreshToken;
    private Boolean authenticated;
}