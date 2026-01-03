package com.pawpplanet.backend.encyclopedia.dto;

import lombok.Data;

@Data
public class BreedResponse {
    private Long id;
    private Long speciesId;
    private String name;
    private String slug;
    private String origin;
    private String shortDescription;
    private String taxonomyType;
    private String avatarUrl;  // Thumbnail for list display
}

