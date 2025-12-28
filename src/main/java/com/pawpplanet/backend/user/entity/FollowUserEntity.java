package com.pawpplanet.backend.user.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "follow_user", schema = "auth")
public class FollowUserEntity {

    @EmbeddedId
    private FollowUserId id;

    @Column(name = "created_at")
    private Instant createdAt;

    public FollowUserEntity() {}

    public FollowUserId getId() { return id; }
    public void setId(FollowUserId id) { this.id = id; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
