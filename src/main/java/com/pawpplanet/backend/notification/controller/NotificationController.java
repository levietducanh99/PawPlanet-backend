package com.pawpplanet.backend.notification.controller;

import com.pawpplanet.backend.common.dto.ApiResponse;
import com.pawpplanet.backend.common.dto.PagedResult;
import com.pawpplanet.backend.notification.dto.NotificationResponse;
import com.pawpplanet.backend.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "API quản lý thông báo")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(
            summary = "Lấy danh sách thông báo",
            description = "Lấy tất cả thông báo của user hiện tại (có phân trang)"
    )
    public ResponseEntity<ApiResponse<PagedResult<NotificationResponse>>> getMyNotifications(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        ApiResponse<PagedResult<NotificationResponse>> response = new ApiResponse<>();
        response.setResult(notificationService.getMyNotifications(page, size));
        response.setStatusCode(0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread")
    @Operation(
            summary = "Lấy thông báo chưa đọc",
            description = "Lấy danh sách thông báo chưa đọc của user hiện tại (có phân trang)"
    )
    public ResponseEntity<ApiResponse<PagedResult<NotificationResponse>>> getMyUnreadNotifications(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        ApiResponse<PagedResult<NotificationResponse>> response = new ApiResponse<>();
        response.setResult(notificationService.getMyUnreadNotifications(page, size));
        response.setStatusCode(0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread/count")
    @Operation(
            summary = "Đếm thông báo chưa đọc",
            description = "Lấy số lượng thông báo chưa đọc của user hiện tại"
    )
    public ResponseEntity<ApiResponse<Long>> getUnreadCount() {
        ApiResponse<Long> response = new ApiResponse<>();
        response.setResult(notificationService.getUnreadCount());
        response.setStatusCode(0);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{notificationId}/read")
    @Operation(
            summary = "Đánh dấu đã đọc",
            description = "Đánh dấu một thông báo là đã đọc"
    )
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long notificationId
    ) {
        notificationService.markAsRead(notificationId);

        ApiResponse<Void> response = new ApiResponse<>();
        response.setMessage("Notification marked as read");
        response.setStatusCode(0);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/read-all")
    @Operation(
            summary = "Đánh dấu tất cả đã đọc",
            description = "Đánh dấu tất cả thông báo là đã đọc"
    )
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        notificationService.markAllAsRead();

        ApiResponse<Void> response = new ApiResponse<>();
        response.setMessage("All notifications marked as read");
        response.setStatusCode(0);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{notificationId}")
    @Operation(
            summary = "Xóa thông báo",
            description = "Xóa một thông báo"
    )
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @PathVariable Long notificationId
    ) {
        notificationService.deleteNotification(notificationId);

        ApiResponse<Void> response = new ApiResponse<>();
        response.setMessage("Notification deleted");
        response.setStatusCode(0);
        return ResponseEntity.ok(response);
    }
}

