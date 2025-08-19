package com.example.datn_qlnt_manager.dto.request.notification;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.example.datn_qlnt_manager.common.NotificationType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationUpdateRequest {

    @NotBlank(message = "NOTIFICATION_TITLE_REQUIRED")
    String title;

    @NotBlank(message = "NOTIFICATION_CONTENT_REQUIRED")
    String content;

    String image;

    @NotNull(message = "NOTIFICATION_TYPE_REQUIRED")
    NotificationType notificationType;

    @NotNull(message = "SEND_TO_ALL_REQUIRED")
    Boolean sendToAll;

    List<String> users;
}
