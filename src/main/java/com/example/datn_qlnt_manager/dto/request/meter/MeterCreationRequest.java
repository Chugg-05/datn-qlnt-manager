package com.example.datn_qlnt_manager.dto.request.meter;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.example.datn_qlnt_manager.common.MeterType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeterCreationRequest {

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
