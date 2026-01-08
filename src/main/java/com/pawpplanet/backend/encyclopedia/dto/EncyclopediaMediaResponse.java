package com.pawpplanet.backend.encyclopedia.dto;

import lombok.Data;

@Data
public class EncyclopediaMediaResponse {
    private Long id;
    private String entityType;  // class, species, breed
    private Long entityId;
    private String type;        // image, video
    private String role;        // hero, gallery, avatar
    private String url;
    private Integer displayOrder;
}

