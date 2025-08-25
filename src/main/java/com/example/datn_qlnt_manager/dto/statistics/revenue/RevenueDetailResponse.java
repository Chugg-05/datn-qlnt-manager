package com.example.datn_qlnt_manager.dto.statistics.revenue;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RevenueDetailResponse {
    BigDecimal roomFee;
    List<ServiceRevenueResponse> services;
}
