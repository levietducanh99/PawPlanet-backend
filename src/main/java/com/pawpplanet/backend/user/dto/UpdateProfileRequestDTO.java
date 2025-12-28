package com.pawpplanet.backend.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequestDTO {
    private String avatarUrl;
    private String bio;


}
