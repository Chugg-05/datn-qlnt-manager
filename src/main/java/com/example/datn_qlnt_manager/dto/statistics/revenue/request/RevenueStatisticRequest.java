package com.example.datn_qlnt_manager.dto.statistics.revenue.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RevenueStatisticRequest {
    String buildingId;

    @Min(value = 1, message = "INVALID_MONTH")
    @Max(value = 12, message = "INVALID_MONTH")
    Integer month;

    Integer year;

}
