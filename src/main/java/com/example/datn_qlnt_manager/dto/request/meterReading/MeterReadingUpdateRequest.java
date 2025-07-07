package com.example.datn_qlnt_manager.dto.request.meterReading;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeterReadingUpdateRequest {

    @NotBlank(message = "Meter code must not be blank")
    String meterCode;

    @NotNull(message = "Old index must not be null")
    private Integer oldIndex;

    @NotNull(message = "New index must not be null")
    private Integer newIndex;

    @NotNull(message = "Month must not be null")
    @Min(value = 1)
    @Max(value = 12)
    private Integer month;

    @NotNull(message = "Year must not be null")
    private Integer year;

    private String description;
}
