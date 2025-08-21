package com.example.datn_qlnt_manager.dto.response.notification;

import java.time.Instant;

import com.example.datn_qlnt_manager.common.NotificationType;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationDetailResponse {
    String id;
    String title;
    String content;
    NotificationType notificationType;
    Boolean sendToAll;
    Instant sentAt;

    String fullName;

    Boolean isRead;
    Instant readAt;
}
