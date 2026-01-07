package com.pawpplanet.backend.notification.repository;

import com.pawpplanet.backend.notification.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    // Get all notifications for a user with pagination
    Page<NotificationEntity> findByRecipientIdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);

    // Get unread notifications for a user
    Page<NotificationEntity> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(Long recipientId, Pageable pageable);

    // Count unread notifications
    Long countByRecipientIdAndIsReadFalse(Long recipientId);

    // Mark notification as read
    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true WHERE n.id = :notificationId AND n.recipientId = :userId")
    int markAsRead(@Param("notificationId") Long notificationId, @Param("userId") Long userId);

    // Mark all notifications as read for a user
    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true WHERE n.recipientId = :userId AND n.isRead = false")
    int markAllAsRead(@Param("userId") Long userId);

    // Delete notification (only if it belongs to the user)
    @Modifying
    @Query("DELETE FROM NotificationEntity n WHERE n.id = :notificationId AND n.recipientId = :userId")
    int deleteByIdAndRecipientId(@Param("notificationId") Long notificationId, @Param("userId") Long userId);
}
