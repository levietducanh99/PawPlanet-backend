package com.pawpplanet.backend.encyclopedia.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "breed_sections", schema = "encyclopedia")
public class BreedSectionEntity {

    @EmbeddedId
    private BreedSectionId id;

    private Integer displayOrder;

    public BreedSectionEntity() {}

    public BreedSectionId getId() { return id; }
    public void setId(BreedSectionId id) { this.id = id; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}
