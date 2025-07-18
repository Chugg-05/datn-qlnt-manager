package com.example.datn_qlnt_manager.dto.filter;

import com.example.datn_qlnt_manager.common.ServiceCalculation;
import com.example.datn_qlnt_manager.common.ServiceStatus;
import com.example.datn_qlnt_manager.common.ServiceCategory;
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
    ServiceCategory serviceType;
    BigDecimal minPrice;
    BigDecimal maxPrice;
    ServiceStatus serviceStatus;
    ServiceCalculation serviceAppliedBy;
}
