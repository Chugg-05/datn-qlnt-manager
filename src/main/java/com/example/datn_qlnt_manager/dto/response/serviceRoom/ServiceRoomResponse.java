package com.example.datn_qlnt_manager.dto.response.serviceRoom;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.example.datn_qlnt_manager.common.ServiceRoomStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceRoomResponse {
    String id;

    String roomCode;

    String serviceName;

    BigDecimal unitPrice;

    LocalDate startDate;

    LocalDate endDate;

    ServiceRoomStatus serviceRoomStatus;

    String description;

    Instant createdAt;

    Instant updatedAt;
}
