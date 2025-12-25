package com.pawpplanet.backend.pet.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "follow_pet", schema = "pet")
public class FollowPetEntity {

    @EmbeddedId
    private FollowPetId id;

    private Instant createdAt;

    public FollowPetEntity() {}

    public FollowPetId getId() { return id; }
    public void setId(FollowPetId id) { this.id = id; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
