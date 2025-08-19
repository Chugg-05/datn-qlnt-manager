package com.example.datn_qlnt_manager.dto.response.notification;

import java.time.LocalDateTime;
import java.util.List;

import com.example.datn_qlnt_manager.common.NotificationType;
import com.example.datn_qlnt_manager.dto.response.IdAndName;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    String notificationId;
    String title;
    String content;
    String image;
    NotificationType notificationType;
    Boolean sendToAll;
    LocalDateTime sentAt;

    // user - người gửi
    String userId;
    String fullName;

    // người nhận
    List<SentToUsers> sentToUsers;

    @Builder
    public NotificationResponse(String notificationId, String title, String content, String image,
                                NotificationType notificationType, Boolean sendToAll, LocalDateTime sentAt,
                                String userId, String fullName) {
        this.notificationId = notificationId;
        this.title = title;
        this.content = content;
        this.image = image;
        this.notificationType = notificationType;
        this.sendToAll = sendToAll;
        this.sentAt = sentAt;
        this.userId = userId;
        this.fullName = fullName;
    }
}
    // user
//    String fullName;
    List<IdAndName> sendToUsers;
    String sender;
}
