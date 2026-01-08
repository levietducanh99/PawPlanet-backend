package com.pawpplanet.backend.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AllPetsResponseDTO {
    private Long id;
    private String name;
    private String avatar;
    private String speciesName;
}
