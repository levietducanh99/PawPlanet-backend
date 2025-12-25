package com.pawpplanet.backend.post.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "post_pet", schema = "social")
public class PostPetEntity {

    @EmbeddedId
    private PostPetId id;

    public PostPetEntity() {}

    public PostPetId getId() { return id; }
    public void setId(PostPetId id) { this.id = id; }
}
