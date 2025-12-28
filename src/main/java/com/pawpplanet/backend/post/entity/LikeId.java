package com.pawpplanet.backend.post.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class LikeId {

    private Long userId;
    private Long postId;

    public LikeId() {}

    public LikeId(Long userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
}
