package com.example.datn_qlnt_manager.dto.statistics.revenue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class InvoiceStatisticsDTO {

    String buildingId;
    String buildingName;
    int year;
    int month;
    BigDecimal expectedRevenue;
    BigDecimal currentRevenue;
    BigDecimal damageAmount;
    BigDecimal overdueAmount;
    BigDecimal compensationAmount;
    BigDecimal depositAmount;
    BigDecimal roomFee;
    BigDecimal energyFee;
    BigDecimal waterFee;
    BigDecimal parkingFee;
    BigDecimal internetFee;
    BigDecimal cleaningFee;
    BigDecimal elevatorFee;
    BigDecimal maintenanceFee;
    BigDecimal securityFee;
    BigDecimal washingFee;
    BigDecimal otherFee;
    BigDecimal compensationFee;
}
