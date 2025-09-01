package com.example.datn_qlnt_manager.dto.statistics.revenue.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MonthlyRevenueComparisonDTO {
    int year;
    int month;
    List<RevenueComparisonResponse> comparisons;
}
