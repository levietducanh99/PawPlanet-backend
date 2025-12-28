package com.pawpplanet.backend.pet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetMediaDTO {
    private Long id;
    private String type;
    private String role;
    private String url;
    private Integer displayOrder;
}