package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.response.systemnotification.SystemNotificationResponse;

public interface SystemNotificationService {
    SystemNotificationResponse createNotification(String userId, String title, String content);
    void deleteNotification(String systemNotificationId);

    void deleteAllNotificationsByUser(String userId);

    PaginatedResponse<SystemNotificationResponse> getNotificationsByCurrentUser(int page, int size);

    PaginatedResponse<SystemNotificationResponse> getUnreadNotificationsByCurrentUser(int page, int size);

    Long countUnreadNotificationsByUser(String userId);

    void markNotificationAsRead(String systemNotificationId);

    void markAllNotificationsAsReadByCurrentUser();





}
