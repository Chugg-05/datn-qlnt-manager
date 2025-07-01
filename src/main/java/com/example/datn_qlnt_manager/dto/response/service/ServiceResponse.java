package com.example.datn_qlnt_manager.dto.response.service;

import com.example.datn_qlnt_manager.common.ServiceAppliedBy;
import com.example.datn_qlnt_manager.common.ServiceStatus;
import com.example.datn_qlnt_manager.common.ServiceType;

import java.math.BigDecimal;

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
