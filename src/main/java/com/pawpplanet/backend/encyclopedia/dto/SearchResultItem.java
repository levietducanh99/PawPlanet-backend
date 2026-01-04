package com.pawpplanet.backend.encyclopedia.dto;

import lombok.Data;

@Data
public class SearchResultItem {
    private String type; // class | species | breed
    private Long id;
    private String name;
    private String slug;
    private String subtitle; // e.g., scientificName or speciesName
    private String avatarUrl;
}

