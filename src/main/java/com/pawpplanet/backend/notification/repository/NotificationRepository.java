package com.pawpplanet.backend.notification.repository;

import com.pawpplanet.backend.notification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
}
