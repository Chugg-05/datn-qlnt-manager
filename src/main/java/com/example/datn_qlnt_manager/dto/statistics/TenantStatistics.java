package com.example.datn_qlnt_manager.dto.statistics;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TenantStatistics {
    long totalTenants;
    long totalRentingTenants;
    long totalCheckedOutTenants;
    long totalPotentialTenants;
    long totalCancelTenants;
    long totalLockedTenants;
}