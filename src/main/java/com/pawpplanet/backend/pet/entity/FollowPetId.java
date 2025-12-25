package com.pawpplanet.backend.pet.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class FollowPetId {

    private Long userId;
    private Long petId;

    public FollowPetId() {}

    public FollowPetId(Long userId, Long petId) {
        this.userId = userId;
        this.petId = petId;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }
}
