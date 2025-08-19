package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.NotificationFilter;
import com.example.datn_qlnt_manager.dto.request.notification.NotificationCreationRequest;
import com.example.datn_qlnt_manager.dto.request.notification.NotificationUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.notification.NotificationDetailResponse;
import com.example.datn_qlnt_manager.dto.response.notification.NotificationResponse;
import org.springframework.web.multipart.MultipartFile;

public interface NotificationService {
    NotificationResponse createNotification(NotificationCreationRequest request, MultipartFile image);

    NotificationResponse updateNotification(String notificationId, NotificationUpdateRequest request, MultipartFile image);

    PaginatedResponse<NotificationResponse> filterMyNotifications(NotificationFilter filter, int page, int size);

    NotificationDetailResponse getNotificationDetail(String notificationId);

    void deleteNotificationById(String notificationId);
}
