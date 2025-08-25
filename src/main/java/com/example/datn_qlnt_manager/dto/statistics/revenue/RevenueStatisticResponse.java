package com.example.datn_qlnt_manager.dto.statistics.revenue;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RevenueStatisticResponse {
    Integer month;
    Integer year;
    String buildingName;
    BigDecimal expectedRevenue;
    BigDecimal actualRevenue;
    BigDecimal lostRevenue;
    BigDecimal overdueAmount;
    BigDecimal compensation;
    RevenueDetailResponse revenueDetail;
}
