package com.pawpplanet.backend.post.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class PostPetId {

    private Long postId;
    private Long petId;

    public PostPetId() {}

    public PostPetId(Long postId, Long petId) {
        this.postId = postId;
        this.petId = petId;
    }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }
}
