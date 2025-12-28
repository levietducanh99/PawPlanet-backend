package com.pawpplanet.backend.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Getter
@Setter
public class UserProfileDTO {
    private Long id;
    private String email;
    private String username;
    private String role;
    private String avatarUrl;
    private String bio;
    private Boolean isVerified;
    private Instant createdAt;
    private Long followersCount;
    private Long followingCount;
    private Long petsCount;


}




