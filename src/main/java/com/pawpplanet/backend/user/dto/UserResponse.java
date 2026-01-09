package com.pawpplanet.backend.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data

public class UserResponse {
    private Long id;
    private String email;
    private String username;
    private String avatarUrl;
    private String bio;
    private String role;
}
