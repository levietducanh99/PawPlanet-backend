package com.pawpplanet.backend.encyclopedia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "species_section_contents", schema = "encyclopedia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpeciesSectionContentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "species_id")
    private Long speciesId;

    @Column(name = "section_id")
    private Long sectionId;

    private String language;

    @Column(columnDefinition = "TEXT")
    private String content;
}

