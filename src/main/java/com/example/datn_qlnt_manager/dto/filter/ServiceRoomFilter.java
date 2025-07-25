package com.example.datn_qlnt_manager.dto.filter;

import java.math.BigDecimal;

import com.example.datn_qlnt_manager.common.ServiceRoomStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceRoomFilter {
    String query; // roomCode, serviceName, usageCode, descriptionServiceRoom
    BigDecimal minPrice;
    BigDecimal maxPrice;
    ServiceRoomStatus status;
}
