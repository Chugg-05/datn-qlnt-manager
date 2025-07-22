package com.example.datn_qlnt_manager.dto.request.meterReading;

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

    @NotNull(message = "NEW_INDEX_NOT_FOUND")
    Integer newIndex;

    String descriptionMeterReading;
}
