package com.pawpplanet.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a Pet.
 * In a real application, this would be a JPA entity with database annotations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pet {

    private Long id;
    private String name;
    private String species;
    private String breed;
    private Integer age;
    private String description;
}
