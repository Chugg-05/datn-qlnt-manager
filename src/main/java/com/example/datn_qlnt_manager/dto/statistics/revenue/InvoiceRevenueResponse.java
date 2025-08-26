package com.example.datn_qlnt_manager.dto.statistics.revenue;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceRevenueResponse {
    BigDecimal expectedRevenue;
    BigDecimal currentRevenue;
    BigDecimal paidRoomFee;
    BigDecimal paidEnergyFee;
    BigDecimal paidWaterFee;
    BigDecimal paidServiceFee;
    BigDecimal compensationAmount;
}
