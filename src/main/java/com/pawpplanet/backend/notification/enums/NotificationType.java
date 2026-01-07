package com.pawpplanet.backend.notification.enums;

/**
 * Notification types - validated at backend only, not in DB
 */
public enum NotificationType {
    FOLLOW_USER,    // Someone followed you
    FOLLOW_PET,     // Someone followed your pet
    LIKE_POST,      // Someone liked your post
    COMMENT_POST,   // Someone commented on your post
    SYSTEM          // System notification
}

