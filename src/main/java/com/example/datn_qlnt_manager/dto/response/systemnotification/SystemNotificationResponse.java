package com.example.datn_qlnt_manager.dto.response.systemnotification;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SystemNotificationResponse {
    String systemNotificationId;
    String userId;
    String title;
    String content;
    LocalDateTime createdAt;
    Boolean isRead;
}
