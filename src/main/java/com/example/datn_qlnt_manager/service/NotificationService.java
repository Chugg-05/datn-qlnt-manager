package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.NotificationFilter;
import com.example.datn_qlnt_manager.dto.request.notification.NotificationCreationRequest;
import com.example.datn_qlnt_manager.dto.request.notification.NotificationUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.notification.NotificationDetailResponse;
import com.example.datn_qlnt_manager.dto.response.notification.NotificationResponse;

public interface NotificationService {
    NotificationResponse createNotification(NotificationCreationRequest request);

    NotificationResponse updateNotification(String notificationId, NotificationUpdateRequest request);

    PaginatedResponse<NotificationResponse> filterMyNotifications(NotificationFilter filter, int page, int size);

    NotificationDetailResponse getNotificationDetail(String notificationId);

    void deleteNotificationById(String notificationId);
}
