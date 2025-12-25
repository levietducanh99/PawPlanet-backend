package com.pawpplanet.backend.encyclopedia.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "breeds", schema = "encyclopedia")
public class BreedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id")
    private SpeciesEntity species;

    private String name;

    private String shortDescription;

    public BreedEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public SpeciesEntity getSpecies() { return species; }
    public void setSpecies(SpeciesEntity species) { this.species = species; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
}
