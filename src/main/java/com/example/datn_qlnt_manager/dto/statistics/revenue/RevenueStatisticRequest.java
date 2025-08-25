package com.example.datn_qlnt_manager.dto.statistics.revenue;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RevenueStatisticRequest {
    Integer month;
    Integer year;
    String buildingId;
}
