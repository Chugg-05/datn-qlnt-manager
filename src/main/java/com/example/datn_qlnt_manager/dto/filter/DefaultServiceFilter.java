package com.example.datn_qlnt_manager.dto.filter;

import java.math.BigDecimal;

import com.example.datn_qlnt_manager.common.DefaultServiceAppliesTo;
import com.example.datn_qlnt_manager.common.DefaultServiceStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DefaultServiceFilter {
    String buildingId;
    String floorId;
    String serviceId;
    DefaultServiceStatus defaultServiceStatus;
    DefaultServiceAppliesTo defaultServiceAppliesTo;
    BigDecimal minPricesApply;
    BigDecimal maxPricesApply;
}
