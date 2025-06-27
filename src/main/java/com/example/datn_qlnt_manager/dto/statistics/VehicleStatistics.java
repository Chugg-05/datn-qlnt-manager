package com.example.datn_qlnt_manager.dto.statistics;

import java.util.Map;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleStatistics {
    private long total;
    private Map<String, Long> byType;
}
