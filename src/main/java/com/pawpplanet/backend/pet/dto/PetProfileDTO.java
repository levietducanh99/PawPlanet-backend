package com.pawpplanet.backend.pet.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


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

    private BigDecimal weight;
    private BigDecimal height;

    // Computed fields (not persisted)
    private boolean isOwner;
    private boolean isFollowing;
    private boolean canFollow;

    // Bổ sung danh sách media
    private List<PetMediaDTO> media;
}