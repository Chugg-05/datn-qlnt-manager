package com.example.datn_qlnt_manager.dto.response.service;

import java.math.BigDecimal;
import java.time.Instant;

import com.example.datn_qlnt_manager.common.ServiceCalculation;
import com.example.datn_qlnt_manager.common.ServiceCategory;
import com.example.datn_qlnt_manager.common.ServiceStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
    String unit;
    BigDecimal price;
    ServiceCategory serviceCategory;
    ServiceCalculation serviceCalculation;
    ServiceStatus status;
    String description;
    Instant createdAt;
    Instant updatedAt;
}
