package com.pawpplanet.backend.encyclopedia.dto;

import lombok.Data;
import java.util.List;

@Data
public class BreedDetailResponse {
    private Long id;
    private Long speciesId;
    private String name;
    private String slug;
    private String origin;
    private String shortDescription;
    private String taxonomyType;

    // Media
    private String heroUrl;       // Hero banner image
    private String thumbnailUrl;  // Main thumbnail
    private List<EncyclopediaMediaResponse> galleryPreview;  // First few images for preview

    // Full details
    private List<BreedAttributeResponse> attributes;
    private List<BreedSectionContentResponse> sections;
}
