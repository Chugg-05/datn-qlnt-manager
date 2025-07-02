package com.example.datn_qlnt_manager.dto.statistics;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TenantStatistics {
    Long totalTenants;
    Long totalRentingTenants;
    Long totalCheckedOutTenants;
    Long totalPotentialTenants;
    Long totalCancelTenants;
    Long totalLockedTenants;
}