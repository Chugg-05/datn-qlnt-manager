package com.example.datn_qlnt_manager.dto.request.meter;

import com.example.datn_qlnt_manager.common.MeterType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeterUpdateRequest {

    String roomId;

    String serviceId;

    @NotNull(message = "METER_TYPE_NOT_FOUND")
    MeterType meterType;

    @NotBlank(message = "METER_NAME_REQUIRED")
    String meterName;

    @NotBlank(message = "METER_CODE_NOT_FOUND")
    String meterCode;

    @NotNull(message = "MANU_FACTURE_DATE_NOT_FOUND")
    LocalDate manufactureDate;

    @NotNull(message = "INITIAL_INDEX_REQUIRED")
    Integer closestIndex;

    String descriptionMeter;
}
