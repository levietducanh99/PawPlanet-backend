package com.pawpplanet.backend.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UserResponse {
    private String email;
    private String username;
    private String avatarUrl;
    private String bio;
    private String role;
}
