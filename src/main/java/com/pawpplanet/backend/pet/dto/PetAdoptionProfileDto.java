package com.pawpplanet.backend.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetAdoptionProfileDto {
    private Long petId;
    private String healthStatus;
    private Boolean vaccinated;
    private Boolean sterilized;
    private String personality;
    private String habits;
    private String favoriteActivities;
    private String careInstructions;
    private String diet;
    private String adoptionRequirements;
    private String reasonForAdoption;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
