package com.pawpplanet.backend.notification.helper;

import com.pawpplanet.backend.notification.enums.NotificationType;
import com.pawpplanet.backend.notification.enums.TargetType;
import com.pawpplanet.backend.notification.service.NotificationService;
import com.pawpplanet.backend.pet.entity.PetEntity;
import com.pawpplanet.backend.post.entity.CommentEntity;
import com.pawpplanet.backend.post.entity.PostEntity;
import com.pawpplanet.backend.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to create notifications with proper metadata
 * Makes it easy to create notifications from anywhere in the code
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationHelper {

    private final NotificationService notificationService;

    /**
     * Someone followed a user
     */
    public void notifyFollowUser(Long recipientId, UserEntity actor) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("actorUsername", actor.getUsername());
        metadata.put("actorAvatar", actor.getAvatarUrl());

        notificationService.createNotification(
                recipientId,
                actor.getId(),
                NotificationType.FOLLOW_USER,
                TargetType.USER,
                actor.getId(),
                metadata
        );
        log.info("Sent FOLLOW_USER notification to user {} from {}", recipientId, actor.getUsername());
    }

    /**
     * Someone followed a pet
     */
    public void notifyFollowPet(Long recipientId, UserEntity actor, PetEntity pet) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("actorUsername", actor.getUsername());
        metadata.put("actorAvatar", actor.getAvatarUrl());
        metadata.put("petName", pet.getName());

        notificationService.createNotification(
                recipientId,
                actor.getId(),
                NotificationType.FOLLOW_PET,
                TargetType.PET,
                pet.getId(),
                metadata
        );
        log.info("Sent FOLLOW_PET notification to user {} for pet {}", recipientId, pet.getName());
    }

    /**
     * Someone liked a post
     */
    public void notifyLikePost(Long recipientId, UserEntity actor, PostEntity post) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("actorUsername", actor.getUsername());
        metadata.put("actorAvatar", actor.getAvatarUrl());
        metadata.put("postId", post.getId());

        // Add post preview if content exists
        if (post.getContent() != null && !post.getContent().isEmpty()) {
            String preview = post.getContent().length() > 50
                    ? post.getContent().substring(0, 50) + "..."
                    : post.getContent();
            metadata.put("postPreview", preview);
        }

        notificationService.createNotification(
                recipientId,
                actor.getId(),
                NotificationType.LIKE_POST,
                TargetType.POST,
                post.getId(),
                metadata
        );
        log.info("Sent LIKE_POST notification to user {} from {}", recipientId, actor.getUsername());
    }

    /**
     * Someone commented on a post
     */
    public void notifyCommentPost(Long recipientId, UserEntity actor, CommentEntity comment, PostEntity post) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("actorUsername", actor.getUsername());
        metadata.put("actorAvatar", actor.getAvatarUrl());
        metadata.put("commentId", comment.getId());
        metadata.put("postId", post.getId());

        // Add comment preview
        if (comment.getContent() != null && !comment.getContent().isEmpty()) {
            String preview = comment.getContent().length() > 100
                    ? comment.getContent().substring(0, 100) + "..."
                    : comment.getContent();
            metadata.put("commentPreview", preview);
        }

        notificationService.createNotification(
                recipientId,
                actor.getId(),
                NotificationType.COMMENT_POST,
                TargetType.COMMENT,
                comment.getId(),
                metadata
        );
        log.info("Sent COMMENT_POST notification to user {} from {}", recipientId, actor.getUsername());
    }

    /**
     * System notification (e.g., account verification, admin messages)
     */
    public void notifySystem(Long recipientId, String message) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("systemMessage", message);

        notificationService.createNotification(
                recipientId,
                null,  // No actor for system notifications
                NotificationType.SYSTEM,
                TargetType.USER,
                recipientId,
                metadata
        );
        log.info("Sent SYSTEM notification to user {}", recipientId);
    }
}

