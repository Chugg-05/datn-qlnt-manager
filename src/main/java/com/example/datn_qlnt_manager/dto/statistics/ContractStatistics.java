package com.example.datn_qlnt_manager.dto.statistics;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractStatistics {
    long totalContracts;
    long totalActiveContracts;
    long totalExpiredContracts;
    long totalAboutToExpireContracts;
    long totalLiquidatedContracts;
    long totalCancelledContracts;
}
