package com.example.datn_qlnt_manager.dto.response.service;

import com.example.datn_qlnt_manager.common.ServiceStatus;
import com.example.datn_qlnt_manager.dto.response.room.RoomBasicResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceDetailResponse {
    String id;
    String name;
    BigDecimal price;
    String unit;
    ServiceStatus status;
    String description;
    List<RoomBasicResponse> rooms;
}
