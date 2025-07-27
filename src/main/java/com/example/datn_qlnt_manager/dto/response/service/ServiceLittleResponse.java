package com.example.datn_qlnt_manager.dto.response.service;

import com.example.datn_qlnt_manager.common.ServiceRoomStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceLittleResponse {
    String id;
    String serviceName;
    BigDecimal unitPrice;
    String unit;
    ServiceRoomStatus serviceRoomStatus;
    String description;
}
