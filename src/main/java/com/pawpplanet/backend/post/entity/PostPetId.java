package com.pawpplanet.backend.post.entity;

import jakarta.persistence.Embeddable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostPetId that = (PostPetId) o;
        return Objects.equals(postId, that.postId) && Objects.equals(petId, that.petId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, petId);
    }
}
