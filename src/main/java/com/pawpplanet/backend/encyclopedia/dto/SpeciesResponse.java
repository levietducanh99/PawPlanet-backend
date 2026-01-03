package com.pawpplanet.backend.encyclopedia.dto;

import lombok.Data;

@Data
public class SpeciesResponse {
    private Long id;
    private Long classId;
    private String name;
    private String slug;
    private String scientificName;
    private String description;
    private String avatarUrl;  // Thumbnail for list display
}

