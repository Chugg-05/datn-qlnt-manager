package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.response.systemnotification.SystemNotificationResponse;
import com.example.datn_qlnt_manager.dto.response.systemnotification.UnreadNotificationCountResponse;
import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.service.SystemNotificationService;
import com.example.datn_qlnt_manager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/system-notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Tag(name = "System Notification", description = "API System Notification")
public class SystemNotificationController {

    SystemNotificationService systemNotificationService;
    UserService userService;

    @Operation(summary = "Tạo thông báo hệ thống")
    @PostMapping
    public ApiResponse<SystemNotificationResponse> createTestNotification(
            @RequestParam String userId,
            @RequestParam String title,
            @RequestParam String content) {
        return ApiResponse.<SystemNotificationResponse>builder()
                .message("Notification has been created!")
                .data(systemNotificationService.createNotification(userId, title, content))
                .build();
    }

    @Operation(summary = "Xóa một thông báo hệ thống theo ID")
    @DeleteMapping("/{systemNotificationId}")
    public ApiResponse<Void> deleteNotification(@PathVariable String systemNotificationId) {
        systemNotificationService.deleteNotification(systemNotificationId);
        return ApiResponse.<Void>builder()
                .message("Notification has been deleted!")
                .build();
    }

    @Operation(summary = "Xóa tất cả thông báo của người dùng hiện tại")
    @DeleteMapping("/delete-all-by-userId")
    public ApiResponse<Void> deleteAllNotifications() {
        User currentUser = userService.getCurrentUser();
        systemNotificationService.deleteAllNotificationsByUser(currentUser.getId());
        return ApiResponse.<Void>builder()
                .message("All notifications have been deleted!")
                .build();
    }

    @Operation(summary = "Lấy danh sách thông báo hệ thống của user hiện tại")
    @GetMapping
    public ApiResponse<PaginatedResponse<SystemNotificationResponse>> getMyNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {

        return ApiResponse.<PaginatedResponse<SystemNotificationResponse>>builder()
                .data(systemNotificationService.getNotificationsByCurrentUser(page, size))
                .message("Get my notifications successfully")
                .build();
    }

    @Operation(summary = "Lấy danh sách thông báo CHƯA ĐỌC của user hiện tại")
    @GetMapping("/unread")
    public ApiResponse<PaginatedResponse<SystemNotificationResponse>> getMyUnreadNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {

        return ApiResponse.<PaginatedResponse<SystemNotificationResponse>>builder()
                .data(systemNotificationService.getUnreadNotificationsByCurrentUser(page, size))
                .message("Get my unread notifications successfully")
                .build();
    }

    @Operation(summary = "Đếm số thông báo chưa đọc của người dùng hiện tại")
    @GetMapping("/unread/count")
    public ApiResponse<UnreadNotificationCountResponse> countUnreadNotifications() {
        User currentUser = userService.getCurrentUser();
        long unreadCount = systemNotificationService.countUnreadNotificationsByUser(currentUser.getId());

        return ApiResponse.<UnreadNotificationCountResponse>builder()
                .message("Get unread notification count successfully")
                .data(UnreadNotificationCountResponse.builder()
                        .totalUnreadNotifications(unreadCount)
                        .build())
                .build();
    }

    @PatchMapping("/read/{notificationId}")
    @Operation(summary = "Đọc 1 thông báo")
    public ApiResponse<Void> markNotificationAsRead(@PathVariable String notificationId) {
        systemNotificationService.markNotificationAsRead(notificationId);
        return ApiResponse.<Void>builder()
                .message("Notification marked as read")
                .build();
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Đọc tất cả thông báo của user")
    public ApiResponse<Void> markAllNotificationsAsRead() {
        systemNotificationService.markAllNotificationsAsReadByCurrentUser();
        return ApiResponse.<Void>builder()
                .message("All notifications marked as read")
                .build();
    }

}
