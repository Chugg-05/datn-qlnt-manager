package com.example.datn_qlnt_manager.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.NotificationFilter;
import com.example.datn_qlnt_manager.dto.request.notification.NotificationCreationRequest;
import com.example.datn_qlnt_manager.dto.request.notification.NotificationUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.notification.NotificationDetailResponse;
import com.example.datn_qlnt_manager.dto.response.notification.NotificationResponse;
import com.example.datn_qlnt_manager.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Notification", description = "API Notification")
public class NotificationController {

    NotificationService notificationService;

    @Operation(summary = "Thêm thông báo")
    @PostMapping
    public ApiResponse<NotificationResponse> createNotification(
            @Valid @RequestBody NotificationCreationRequest request) {
        return ApiResponse.<NotificationResponse>builder()
                .data(notificationService.createNotification(request))
                .message("Create Notification successfully")
                .build();
    }

    @Operation(summary = "Cập nhật thông báo")
    @PutMapping("/{notificationId}")
    public ApiResponse<NotificationResponse> updateNotification(
            @PathVariable String notificationId, @Valid @RequestBody NotificationUpdateRequest request) {
        return ApiResponse.<NotificationResponse>builder()
                .data(notificationService.updateNotification(notificationId, request))
                .message("Update Notification successfully")
                .build();
    }

    @Operation(summary = "Hiển thị, lọc, tìm kiếm thông báo của người dùng hiện tại")
    @GetMapping
    public ApiResponse<List<NotificationResponse>> getMyNotifications(
            @Valid @ModelAttribute NotificationFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        PaginatedResponse<NotificationResponse> result = notificationService.filterMyNotifications(filter, page, size);
        return ApiResponse.<List<NotificationResponse>>builder()
                .message("Notification data retrieved successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Xem chi tiết thông báo của người dùng hiện tại")
    @GetMapping("/{notificationId}")
    public ApiResponse<NotificationDetailResponse> getNotificationDetail(@PathVariable String notificationId) {
        return ApiResponse.<NotificationDetailResponse>builder()
                .data(notificationService.getNotificationDetail(notificationId))
                .message("Notification detail retrieved successfully")
                .build();
    }

    @Operation(summary = "Xóa thông báo")
    @DeleteMapping("/{notificationId}")
    public ApiResponse<Void> deleteNotification(@PathVariable("notificationId") String notificationId) {
        notificationService.deleteNotificationById(notificationId);
        return ApiResponse.<Void>builder()
                .message("Notification deleted successfully")
                .build();
    }
}
