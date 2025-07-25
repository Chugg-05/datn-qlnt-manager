package com.example.datn_qlnt_manager.dto.response.meter;

import com.example.datn_qlnt_manager.common.MeterType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeterReadingMonthlyStatsResponse {
    String meterCode;
    Integer month;
    Integer year;
    Integer oldIndex;
    Integer newIndex;
    Integer quantity;
    MeterType meterType;
}
