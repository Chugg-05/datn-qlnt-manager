package com.example.datn_qlnt_manager.dto.response.meterReading;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeterReadingResponse {
    String id;
    String meterCode;
    Integer oldIndex;
    Integer newIndex;
    Integer quantity;
    Integer month;
    Integer year;
    LocalDateTime readingDate;
    String description;
}

