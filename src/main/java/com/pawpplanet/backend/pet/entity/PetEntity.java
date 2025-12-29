package com.pawpplanet.backend.pet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "pets", schema = "pet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "species_id")
    private Long speciesId;

    @Column(name = "breed_id")
    private Long breedId;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private String gender;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String status;  // owned | for_adoption

    @Column(name = "owner_id")
    private Long ownerId;
}