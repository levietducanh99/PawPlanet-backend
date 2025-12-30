package com.pawpplanet.backend.pet.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdatePetRequestDTO {
    private String name;

    private Long speciesId;

    private Long breedId;

    private LocalDate birthDate;

    private String gender;

    private String description;

    private String status;

    private String url;
}
