package com.pawpplanet.backend.notification.service.impl;

import com.pawpplanet.backend.notification.entity.NotificationEntity;
import com.pawpplanet.backend.notification.repository.NotificationRepository;
import com.pawpplanet.backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void createNotification(Long targetUserId, String type, Long postId) {
        // Updated to use V9 notification schema
        // This is a basic implementation - you may want to add more details to metadata
        NotificationEntity notification = NotificationEntity.builder()
                .recipientId(targetUserId)
                .type(type)
                .targetType("POST")  // Assuming this is always POST for now
                .targetId(postId)
                .metadata("{}")  // Empty JSON for now - should be populated with actual data
                .build();

        notificationRepository.save(notification);
    }
}
