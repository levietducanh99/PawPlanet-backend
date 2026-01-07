package com.pawpplanet.backend.notification.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawpplanet.backend.common.dto.PagedResult;
import com.pawpplanet.backend.common.exception.AppException;
import com.pawpplanet.backend.common.exception.ErrorCode;
import com.pawpplanet.backend.notification.dto.NotificationResponse;
import com.pawpplanet.backend.notification.entity.NotificationEntity;
import com.pawpplanet.backend.notification.enums.NotificationType;
import com.pawpplanet.backend.notification.enums.TargetType;
import com.pawpplanet.backend.notification.repository.NotificationRepository;
import com.pawpplanet.backend.notification.service.NotificationService;
import com.pawpplanet.backend.user.entity.UserEntity;
import com.pawpplanet.backend.user.repository.UserRepository;
import com.pawpplanet.backend.utils.SecurityHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SecurityHelper securityHelper;
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

    @Override
    public PagedResult<NotificationResponse> getMyNotifications(int page, int size) {
        UserEntity currentUser = securityHelper.getCurrentUser();
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<NotificationEntity> notificationPage = notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(currentUser.getId(), pageable);

        List<NotificationResponse> items = notificationPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        PagedResult<NotificationResponse> result = new PagedResult<>();
        result.setItems(items);
        result.setTotalElements(notificationPage.getTotalElements());
        result.setPage(page);
        result.setSize(size);
        return result;
    }

    @Override
    public PagedResult<NotificationResponse> getMyUnreadNotifications(int page, int size) {
        UserEntity currentUser = securityHelper.getCurrentUser();
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<NotificationEntity> notificationPage = notificationRepository
                .findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(currentUser.getId(), pageable);

        List<NotificationResponse> items = notificationPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        PagedResult<NotificationResponse> result = new PagedResult<>();
        result.setItems(items);
        result.setTotalElements(notificationPage.getTotalElements());
        result.setPage(page);
        result.setSize(size);
        return result;
    }

    @Override
    public Long getUnreadCount() {
        UserEntity currentUser = securityHelper.getCurrentUser();
        return notificationRepository.countByRecipientIdAndIsReadFalse(currentUser.getId());
    }

    @Override
    public void markAsRead(Long notificationId) {
        UserEntity currentUser = securityHelper.getCurrentUser();
        int updated = notificationRepository.markAsRead(notificationId, currentUser.getId());

        if (updated == 0) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }

    @Override
    public void markAllAsRead() {
        UserEntity currentUser = securityHelper.getCurrentUser();
        notificationRepository.markAllAsRead(currentUser.getId());
    }

    @Override
    public void deleteNotification(Long notificationId) {
        UserEntity currentUser = securityHelper.getCurrentUser();
        int deleted = notificationRepository.deleteByIdAndRecipientId(notificationId, currentUser.getId());

        if (deleted == 0) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }

    /**
     * Map NotificationEntity to NotificationResponse DTO
     */
    private NotificationResponse mapToResponse(NotificationEntity entity) {
        NotificationResponse response = new NotificationResponse();
        response.setId(entity.getId());
        response.setType(entity.getType());
        response.setIsRead(entity.getIsRead());
        response.setCreatedAt(entity.getCreatedAt());

        // Map actor info
        if (entity.getActorId() != null) {
            UserEntity actor = userRepository.findById(entity.getActorId()).orElse(null);
            if (actor != null) {
                NotificationResponse.ActorInfo actorInfo = new NotificationResponse.ActorInfo();
                actorInfo.setId(actor.getId());
                actorInfo.setUsername(actor.getUsername());
                actorInfo.setAvatarUrl(actor.getAvatarUrl());
                response.setActor(actorInfo);
            }
        }

        // Map target info
        NotificationResponse.TargetInfo targetInfo = new NotificationResponse.TargetInfo();
        targetInfo.setType(entity.getTargetType());
        targetInfo.setId(entity.getTargetId());
        response.setTarget(targetInfo);

        // Parse metadata from JSON string to Map
        try {
            if (entity.getMetadata() != null && !entity.getMetadata().isEmpty()) {
                Map<String, Object> metadata = objectMapper.readValue(
                        entity.getMetadata(),
                        new TypeReference<Map<String, Object>>() {}
                );
                response.setMetadata(metadata);
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to parse notification metadata", e);
        }

        return response;
    }
}
