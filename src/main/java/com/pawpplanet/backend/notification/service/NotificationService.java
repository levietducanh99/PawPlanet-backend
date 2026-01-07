package com.pawpplanet.backend.notification.service;

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
}
