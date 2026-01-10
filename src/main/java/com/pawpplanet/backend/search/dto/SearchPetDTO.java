package com.pawpplanet.backend.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchPetDTO {
    private Long id;
    private String name;
    private String species;
    private String breed;
    private String avatarUrl;
}
