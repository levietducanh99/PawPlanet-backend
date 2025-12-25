package com.pawpplanet.backend.post.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "likes", schema = "social")
public class LikeEntity {

    @EmbeddedId
    private LikeId id;

    private Instant createdAt;

    public LikeEntity() {}

    public LikeId getId() { return id; }
    public void setId(LikeId id) { this.id = id; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
