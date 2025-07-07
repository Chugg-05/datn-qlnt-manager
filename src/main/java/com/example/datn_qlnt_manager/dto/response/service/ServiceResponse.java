package com.example.datn_qlnt_manager.dto.response.service;

import com.example.datn_qlnt_manager.common.ServiceAppliedBy;
import com.example.datn_qlnt_manager.common.ServiceStatus;
import com.example.datn_qlnt_manager.common.ServiceType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
}
