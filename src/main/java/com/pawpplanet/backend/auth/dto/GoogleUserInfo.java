package com.pawpplanet.backend.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleUserInfo {
    private String sub;           // Google user ID (provider_user_id)
    private String email;
    private Boolean emailVerified;
    private String name;
    private String picture;       // Avatar URL
}
