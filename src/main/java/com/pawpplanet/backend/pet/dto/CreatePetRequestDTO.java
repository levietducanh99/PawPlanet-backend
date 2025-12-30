package com.pawpplanet.backend.pet.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class CreatePetRequestDTO {
    private String name;

    private Long speciesId;

    private Long breedId;

    private LocalDate birthDate;

    private String gender;

    private String description;

    // owned | for_adoption
    private String status;

    private String url;


}
