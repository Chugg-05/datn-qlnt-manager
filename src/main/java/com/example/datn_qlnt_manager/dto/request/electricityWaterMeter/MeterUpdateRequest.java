package com.example.datn_qlnt_manager.dto.request.electricityWaterMeter;

import com.example.datn_qlnt_manager.common.MeterType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeterUpdateRequest {

    @NotBlank(message = "Room ID must not be blank")
    private String roomCode;

    private String serviceId;

    @NotNull(message = "Meter type must not be null")
    private MeterType meterType;

    @NotBlank(message = "Meter name must not be blank")
    private String meterName;

    @NotBlank(message = "Meter code must not be blank")
    private String meterCode;

    private LocalDate manufactureDate;

    @NotNull(message = "Initial index must not be null")
    private Integer initialIndex;

    private String description;
}
