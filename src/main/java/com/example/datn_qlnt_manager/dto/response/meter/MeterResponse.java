package com.example.datn_qlnt_manager.dto.response.meter;

import java.time.Instant;
import java.time.LocalDate;

import com.example.datn_qlnt_manager.common.MeterType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeterResponse {
    String id;
    String roomId;
    String roomCode;
    String serviceId;
    String serviceName;
    MeterType meterType;
    String meterName;
    String meterCode;
    LocalDate manufactureDate;
    Integer initialIndex;
    String descriptionMeter;
    Instant createdAt;
    Instant updatedAt;
}
