package com.pawpplanet.backend.notification.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response DTO for frontend - clean and structured
 */
@Data
public class NotificationResponse {
    private Long id;
    private String type;
    private Boolean isRead;
    private LocalDateTime createdAt;

    // Actor info (who triggered the notification)
    private ActorInfo actor;

    // Target info (where to navigate)
    private TargetInfo target;

    // Flexible metadata
    private Map<String, Object> metadata;

    @Data
    public static class ActorInfo {
        private Long id;
        private String username;
        private String avatarUrl;
    }

    @Data
    public static class TargetInfo {
        private String type;  // USER, PET, POST, COMMENT
        private Long id;
    }
}

