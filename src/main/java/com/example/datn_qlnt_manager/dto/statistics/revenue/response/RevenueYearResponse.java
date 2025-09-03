package com.example.datn_qlnt_manager.dto.statistics.revenue.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RevenueYearResponse {
    int year;
    String buildingId; // optional
    BigDecimal totalAmount;
    List<MonthlyRevenueResponse> months;
}
