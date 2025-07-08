package com.example.datn_qlnt_manager.dto.response.service;

import com.example.datn_qlnt_manager.common.ServiceAppliedBy;
import com.example.datn_qlnt_manager.common.ServiceStatus;
import com.example.datn_qlnt_manager.common.ServiceType;
import com.example.datn_qlnt_manager.dto.response.UserResponse;
import com.example.datn_qlnt_manager.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceResponse {
    String id;
    String name;
    ServiceType type;
    String unit;
    BigDecimal price;
    ServiceAppliedBy appliedBy;
    ServiceStatus status;
    String description;
    UserResponse user;
    Instant createdAt;
    Instant updatedAt;
}
