package com.example.datn_qlnt_manager.dto.request.vehicle;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;

import com.example.datn_qlnt_manager.common.VehicleStatus;
import com.example.datn_qlnt_manager.common.VehicleType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleCreationRequest {
    @NotBlank(message = "INVALID_TENANT_ID_BLANK")
    String tenantId;

    @NotNull(message = "INVALID_VEHICLE_TYPE_BLANK")
    VehicleType vehicleType;

    String licensePlate;

    @NotNull(message = "INVALID_VEHICLE_STATUS_BLANK")
    VehicleStatus vehicleStatus;

    @NotNull(message = "INVALID_REGISTRATION_DATE_BLANK")
    @PastOrPresent(message = "INVALID_REGISTRATION_DATE")
    Date registrationDate;

    String describe;
}
