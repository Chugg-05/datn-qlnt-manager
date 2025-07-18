package com.example.datn_qlnt_manager.dto.request.meterReading;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeterReadingCreationRequest {

    @NotBlank(message = "METER_NOT_FOUND")
    String meterId;

    @NotNull(message = "NEW_INDEX_NOT_FOUND")
    Integer newIndex;

    @Min(value = 1, message = "MONTH_GREATER")
    @Max(value = 12,message = "MONTH_LESS")
    Integer month;

    @Min(value = 1,message = "YEAR_GREATER")
    Integer year;

    @NotNull(message = "READING_DATE_NOT_FOUND")
    LocalDate readingDate;

    String descriptionMeterReading;

}
