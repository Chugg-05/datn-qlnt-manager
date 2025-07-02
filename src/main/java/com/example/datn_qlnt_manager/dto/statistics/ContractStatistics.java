package com.example.datn_qlnt_manager.dto.statistics;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractStatistics {
    Long totalContracts;
    Long totalActiveContracts;
    Long totalExpiredContracts;
    Long totalAboutToExpireContracts;
    Long totalLiquidatedContracts;
    Long totalCancelledContracts;
}
