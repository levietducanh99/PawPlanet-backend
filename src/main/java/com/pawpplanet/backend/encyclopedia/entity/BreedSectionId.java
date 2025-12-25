package com.pawpplanet.backend.encyclopedia.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class BreedSectionId {

    private Long breedId;
    private Long sectionId;

    public BreedSectionId() {}

    public BreedSectionId(Long breedId, Long sectionId) {
        this.breedId = breedId;
        this.sectionId = sectionId;
    }

    public Long getBreedId() { return breedId; }
    public void setBreedId(Long breedId) { this.breedId = breedId; }

    public Long getSectionId() { return sectionId; }
    public void setSectionId(Long sectionId) { this.sectionId = sectionId; }
}
