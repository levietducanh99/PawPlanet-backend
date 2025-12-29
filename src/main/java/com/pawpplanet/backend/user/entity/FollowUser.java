package com.pawpplanet.backend.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "follow_user", schema = "auth")
@Getter
@Setter
public class FollowUser {

    @EmbeddedId
    private FollowUserId id;

    @MapsId("followerId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    private UserEntity follower;

    @MapsId("followingId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id")
    private UserEntity following;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
