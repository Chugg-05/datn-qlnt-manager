package com.example.datn_qlnt_manager.dto.filter;

import com.example.datn_qlnt_manager.common.MeterType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeterFilter {
    String roomId;
    MeterType meterType;
}
