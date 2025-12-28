package com.pawpplanet.backend.encyclopedia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Master table for breed sections (nutrition, training, health, etc.)
 * Maps to encyclopedia.breed_sections table
 */
@Entity
@Table(name = "breed_sections", schema = "encyclopedia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BreedSectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(name = "display_name")
    private String displayName;

    @Column(columnDefinition = "TEXT")
    private String description;
}
