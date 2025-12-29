package com.pawpplanet.backend.pet.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class PetProfileDTO {
    private Long id;
    private String name;
    private Long speciesId;
    private String speciesName;
    private Long breedId;
    private String breedName;
    private LocalDate birthDate;
    private String gender;
    private String description;
    private String status;
    private Long ownerId;
    private String ownerUsername;

    // Bổ sung danh sách media
//    private List<PetMediaDTO> media;
}