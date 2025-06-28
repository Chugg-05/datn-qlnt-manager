package com.example.datn_qlnt_manager.dto.request.meter;

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
public class MeterCreationRequest {

    @NotBlank(message = "Room code must not be blank")
    private String roomCode;

    private String serviceCode;

    @NotNull(message = "Meter type must not be null")
    private MeterType meterType;

    @NotBlank(message = "Meter name must not be blank")
    private String meterName;

    @NotBlank(message = "Meter ID must not be blank")
    private String meterCode;

    private LocalDate manufactureDate;

    @NotNull(message = "Initial index must not be null")
    private Integer initialIndex;

    private String description;
}
