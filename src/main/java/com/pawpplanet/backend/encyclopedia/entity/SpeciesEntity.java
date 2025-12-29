package com.pawpplanet.backend.encyclopedia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "species", schema = "encyclopedia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpeciesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_id")
    private Long classId;

    @Column(nullable = false)
    private String name;

    @Column(name = "scientific_name")
    private String scientificName;

    @Column(columnDefinition = "TEXT")
    private String description;
}