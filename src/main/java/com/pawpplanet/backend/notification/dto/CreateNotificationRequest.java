package com.pawpplanet.backend.notification.dto;

import com.pawpplanet.backend.notification.enums.NotificationType;
import com.pawpplanet.backend.notification.enums.TargetType;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Request to create a notification
 */
@Data
@Builder
public class CreateNotificationRequest {
    private Long recipientId;
    private Long actorId;
    private NotificationType type;
    private TargetType targetType;
    private Long targetId;
    private Map<String, Object> metadata;
}

