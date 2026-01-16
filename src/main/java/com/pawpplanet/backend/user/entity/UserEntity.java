package com.pawpplanet.backend.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users", schema = "auth")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String role;

    @Column(name = "auth_provider", nullable = false)
    private String authProvider = "LOCAL";

    @Column(name = "provider_user_id")
    private String providerUserId;

    @Column(name = "email_verified")
    private Boolean emailVerified;

    @Column(name = "full_name")
    private String fullName;  // Tên đầy đủ của user

    @Column(name = "avatar_public_id", columnDefinition = "TEXT")
    private String avatarPublicId;  // Cloudinary public_id for avatar

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    @Column(name = "cover_image_public_id", columnDefinition = "TEXT")
    private String coverImagePublicId;  // Cloudinary public_id for cover image

    @Column(name = "cover_image_url", columnDefinition = "TEXT")
    private String coverImageUrl;  // Ảnh bìa profile

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Note: Following/Followers relationships are managed through FollowUserEntity
    // To get following/followers, query FollowUserEntity by follower_id or following_id

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isVerified == null) {
            isVerified = false;
        }
        if (authProvider == null) {
            authProvider = "LOCAL";
        }
        if (emailVerified == null) {
            emailVerified = false;
        }
    }
}
