package com.example.datn_qlnt_manager.dto.response.notification;

import com.example.datn_qlnt_manager.common.NotificationType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    String notificationId;
    String title;
    String content;
    NotificationType notificationType;
    Boolean sendToAll;
    LocalDateTime sentAt;

    // user
    String fullName;
}
