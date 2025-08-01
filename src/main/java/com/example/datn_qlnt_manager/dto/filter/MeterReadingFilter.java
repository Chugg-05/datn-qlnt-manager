package com.example.datn_qlnt_manager.dto.filter;

import com.example.datn_qlnt_manager.common.MeterType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeterReadingFilter {
    String buildingId;
    String roomId;
    MeterType meterType;
    Integer month;
}
