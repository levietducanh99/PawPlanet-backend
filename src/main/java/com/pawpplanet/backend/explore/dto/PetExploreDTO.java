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
public class PetExploreDTO {
    private Long id;
    private String name;
    private String species;
    private String breed;
    private String avatarUrl;
    private OwnerDTO owner;
    private Long followerCount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OwnerDTO {
        private Long id;
        private String username;
    }
}

