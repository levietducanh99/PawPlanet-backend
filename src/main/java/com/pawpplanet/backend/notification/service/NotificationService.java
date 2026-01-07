package com.pawpplanet.backend.notification.service;

import com.pawpplanet.backend.common.dto.PagedResult;
import com.pawpplanet.backend.notification.dto.NotificationResponse;
import com.pawpplanet.backend.notification.enums.NotificationType;
import com.pawpplanet.backend.notification.enums.TargetType;

import java.util.Map;

public interface NotificationService {
    /**
     * Create a notification with full details
     */
    void createNotification(
            Long recipientId,
            Long actorId,
            NotificationType type,
            TargetType targetType,
            Long targetId,
            Map<String, Object> metadata
    );

    /**
     * Get all notifications for current user (paginated)
     */
    PagedResult<NotificationResponse> getMyNotifications(int page, int size);

    /**
     * Get unread notifications for current user (paginated)
     */
    PagedResult<NotificationResponse> getMyUnreadNotifications(int page, int size);

    /**
     * Get count of unread notifications for current user
     */
    Long getUnreadCount();

    /**
     * Mark a notification as read
     */
    void markAsRead(Long notificationId);

    /**
     * Mark all notifications as read for current user
     */
    void markAllAsRead();

    /**
     * Delete a notification
     */
    void deleteNotification(Long notificationId);
}
