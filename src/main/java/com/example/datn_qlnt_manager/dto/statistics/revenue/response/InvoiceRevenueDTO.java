package com.example.datn_qlnt_manager.dto.statistics.revenue.response;

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
public class InvoiceRevenueDTO {
    BigDecimal roomFee;
    BigDecimal energyFee;
    BigDecimal waterFee;
    BigDecimal serviceFee;
    BigDecimal compensation;
    BigDecimal deposit;
    BigDecimal expectedRevenue;
    BigDecimal actualRevenue;
    BigDecimal overdueAmount;
    BigDecimal damageAmount;
}
