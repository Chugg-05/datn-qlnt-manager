package com.example.datn_qlnt_manager.dto.response.meter;

import com.example.datn_qlnt_manager.common.MeterType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeterResponse {
    String id;
    String roomCode;
    String serviceCode;
    MeterType meterType;
    String meterName;
    String meterId;
    LocalDate manufactureDate;
    Integer initialIndex;
    String description;
}
