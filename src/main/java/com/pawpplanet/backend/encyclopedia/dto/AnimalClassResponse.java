package com.pawpplanet.backend.encyclopedia.dto;

import lombok.Data;

@Data
public class AnimalClassResponse {
    private Long id;
    private String name;
    private String code;
    private String slug;
    private String description;
    private String avatarUrl;  // Thumbnail for list display
}

