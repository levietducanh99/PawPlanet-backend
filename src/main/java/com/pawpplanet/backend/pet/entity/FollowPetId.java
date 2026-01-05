package com.pawpplanet.backend.pet.entity;

import jakarta.persistence.Embeddable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FollowPetId that = (FollowPetId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(petId, that.petId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, petId);
    }
}
