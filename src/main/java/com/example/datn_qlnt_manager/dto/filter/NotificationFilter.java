package com.example.datn_qlnt_manager.dto.filter;

import java.time.LocalDateTime;

import com.example.datn_qlnt_manager.common.NotificationType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationFilter {
    private String query;
    private NotificationType notificationType;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}
