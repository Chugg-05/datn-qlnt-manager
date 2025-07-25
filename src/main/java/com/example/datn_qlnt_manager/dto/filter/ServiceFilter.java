package com.example.datn_qlnt_manager.dto.filter;

import java.math.BigDecimal;

import com.example.datn_qlnt_manager.common.ServiceCalculation;
import com.example.datn_qlnt_manager.common.ServiceCategory;
import com.example.datn_qlnt_manager.common.ServiceStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceFilter {
    String query;
    ServiceCategory serviceCategory;
    BigDecimal minPrice;
    BigDecimal maxPrice;
    ServiceStatus serviceStatus;
    ServiceCalculation serviceCalculation;
}
