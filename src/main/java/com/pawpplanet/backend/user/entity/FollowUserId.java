package com.pawpplanet.backend.user.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class FollowUserId {

    private Long followerId;
    private Long followingId;

    public FollowUserId() {}

    public FollowUserId(Long followerId, Long followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
    }

    public Long getFollowerId() { return followerId; }
    public void setFollowerId(Long followerId) { this.followerId = followerId; }

    public Long getFollowingId() { return followingId; }
    public void setFollowingId(Long followingId) { this.followingId = followingId; }
}
