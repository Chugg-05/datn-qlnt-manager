package com.example.datn_qlnt_manager.dto.response.systemnotification;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UnreadNotificationCountResponse {
    Long totalUnreadNotifications;
}
