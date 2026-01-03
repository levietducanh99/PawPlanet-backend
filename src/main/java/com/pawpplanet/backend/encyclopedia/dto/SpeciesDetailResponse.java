package com.pawpplanet.backend.encyclopedia.dto;

import lombok.Data;
import java.util.List;

@Data
public class SpeciesDetailResponse {
    private Long id;
    private Long classId;
    private String name;
    private String slug;
    private String scientificName;
    private String description;

    // Media
    private String heroUrl;       // Hero banner image
    private String thumbnailUrl;  // Main thumbnail
    private List<EncyclopediaMediaResponse> galleryPreview;  // First few images for preview

    // Full details
    private List<SpeciesAttributeResponse> attributes;
    private List<SpeciesSectionContentResponse> sections;
}

