package com.pawpplanet.backend.encyclopedia.dto;

import lombok.Data;

@Data
public class BreedAttributeResponse {
    private Long id;
    private Long breedId;
    private String key;
    private String value;
    private Integer displayOrder;
}

