package com.example.datn_qlnt_manager.dto.request.contractVehicle;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddVehicleToContractRequest {

    @NotBlank(message = "CONTRACT_NOT_FOUND")
    String contractId;

    @NotBlank(message = "VEHICLE_NOT_FOUND")
    String vehicleId;
}
