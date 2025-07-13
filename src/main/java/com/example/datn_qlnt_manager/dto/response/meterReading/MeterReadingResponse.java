package com.example.datn_qlnt_manager.dto.response.meterReading;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeterReadingResponse {
    String id;

    String meterId;
    String meterCode;
    String meterName;
    String meterType;

    Integer oldIndex;
    Integer newIndex;
    Integer quantity;
    Integer month;
    Integer year;
    LocalDate readingDate;
    String descriptionMeterReading;
}

