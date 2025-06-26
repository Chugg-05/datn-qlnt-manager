package com.example.datn_qlnt_manager.dto.request.vehicle;

import jakarta.validation.constraints.NotNull;

import com.example.datn_qlnt_manager.common.VehicleStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleUpdateRequest {
    @NotNull(message = "INVALID_VEHICLE_STATUS_BLANK")
    VehicleStatus vehicleStatus;

    String describe;
}
