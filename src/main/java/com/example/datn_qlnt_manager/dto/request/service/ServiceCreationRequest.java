package com.example.datn_qlnt_manager.dto.request.service;

import com.example.datn_qlnt_manager.common.ServiceAppliedBy;
import com.example.datn_qlnt_manager.common.ServiceStatus;
import com.example.datn_qlnt_manager.common.ServiceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ServiceCreationRequest {
    @NotBlank
    String name;

    @NotNull
    ServiceType type;

    @NotBlank
    String unit;

    @NotNull
    BigDecimal price;

    ServiceAppliedBy appliedBy;

    @NotNull
    ServiceStatus status;

    String description;
}
