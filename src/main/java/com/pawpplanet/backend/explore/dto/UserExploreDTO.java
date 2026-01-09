package com.pawpplanet.backend.explore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserExploreDTO {
    private Long id;
    private String username;
    private String avatarUrl;
    private Long petCount;
    private Long followerCount;
}

