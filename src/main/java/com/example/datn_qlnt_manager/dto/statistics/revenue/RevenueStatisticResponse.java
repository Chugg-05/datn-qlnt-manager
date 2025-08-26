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
    String buildingId;
    String buildingName;
    Integer year;
    Integer month;
    BigDecimal expectedRevenue;
    BigDecimal currentRevenue;
    BigDecimal paidRoomFee;
    BigDecimal paidEnergyFee;
    BigDecimal paidWaterFee;
    BigDecimal paidServiceFee;
    BigDecimal compensationAmount;
    BigDecimal damageAmount;
    BigDecimal overdueAmount;
    BigDecimal unreturnedDeposit;
}
