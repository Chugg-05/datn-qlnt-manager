package com.example.datn_qlnt_manager.dto.response.serviceRoom;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.datn_qlnt_manager.common.ServiceRoomStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceRoomResponse {
    String id;

    String roomId;
    String roomCode;

    String serviceId;
    String name;

    String usageCode;

    LocalDateTime applyTime;

    LocalDate startDate;

    BigDecimal totalPrice;

    ServiceRoomStatus serviceRoomStatus;

    String descriptionServiceRoom;

    Instant createdAt;

    Instant updatedAt;
}
