package com.example.datn_qlnt_manager.dto.filter;

import com.example.datn_qlnt_manager.common.ServiceAppliedBy;
import com.example.datn_qlnt_manager.common.ServiceStatus;
import com.example.datn_qlnt_manager.common.ServiceType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceFilter {
    String query;
    ServiceType serviceType;
    BigDecimal minPrice;
    BigDecimal maxPrice;
    ServiceStatus serviceStatus;
    ServiceAppliedBy serviceAppliedBy;
}
