package com.pawpplanet.backend.encyclopedia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "breeds", schema = "encyclopedia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BreedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "species_id")
    private Long speciesId;

    @Column(nullable = false)
    private String name;

    private String origin;

    @Column(name = "short_description", columnDefinition = "TEXT")
    private String shortDescription;

    @Column(name = "taxonomy_type", nullable = false)
    private String taxonomyType = "breed"; // "breed" or "subspecies"
}