package com.example.datn_qlnt_manager.dto.response.vehicle;

import java.util.Map;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleStatisticsResponse {
    private long total;
    private Map<String, Long> byType;
}
