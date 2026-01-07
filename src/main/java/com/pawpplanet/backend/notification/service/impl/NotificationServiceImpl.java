package com.pawpplanet.backend.notification.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawpplanet.backend.notification.entity.NotificationEntity;
import com.pawpplanet.backend.notification.enums.NotificationType;
import com.pawpplanet.backend.notification.enums.TargetType;
import com.pawpplanet.backend.notification.repository.NotificationRepository;
import com.pawpplanet.backend.notification.service.NotificationService;
import com.pawpplanet.backend.user.repository.UserRepository;
import com.pawpplanet.backend.utils.SecurityHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void createNotification(
            Long recipientId,
            Long actorId,
            NotificationType type,
            TargetType targetType,
            Long targetId,
            Map<String, Object> metadata
    ) {
        try {
            // Convert metadata Map to JSON string for JSONB storage
            String metadataJson = metadata != null && !metadata.isEmpty()
                    ? objectMapper.writeValueAsString(metadata)
                    : "{}";

            NotificationEntity notification = NotificationEntity.builder()
                    .recipientId(recipientId)
                    .actorId(actorId)
                    .type(type.name())  // Convert enum to string
                    .targetType(targetType.name())  // Convert enum to string
                    .targetId(targetId)
                    .metadata(metadataJson)
                    .build();

            notificationRepository.save(notification);
            log.info("Created notification: type={}, recipient={}, actor={}", type, recipientId, actorId);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize notification metadata", e);
            throw new RuntimeException("Failed to create notification", e);
        }
    }
}
