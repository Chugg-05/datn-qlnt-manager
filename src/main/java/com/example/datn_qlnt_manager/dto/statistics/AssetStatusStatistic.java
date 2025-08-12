package com.example.datn_qlnt_manager.dto.statistics;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssetStatusStatistic {
    Long totalAssets;
    Long totalActiveAssets;
    Long totalBrokenAssets;
    Long totalMaintenanceAssets;
    Long totalLostAssets;
    Long totalDisabledAssets;
}
