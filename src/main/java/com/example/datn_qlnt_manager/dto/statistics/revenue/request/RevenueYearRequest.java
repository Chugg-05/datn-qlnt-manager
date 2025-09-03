package com.example.datn_qlnt_manager.dto.statistics.revenue.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RevenueYearRequest {
    Integer year;
    String buildingId;
}
