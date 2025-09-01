package com.example.datn_qlnt_manager.dto.statistics.revenue.response;

import com.example.datn_qlnt_manager.common.RevenueCategory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RevenueComparisonResponse {
    RevenueCategory category;
    BigDecimal current;
    BigDecimal previous;
    BigDecimal difference;
    BigDecimal percent;
}