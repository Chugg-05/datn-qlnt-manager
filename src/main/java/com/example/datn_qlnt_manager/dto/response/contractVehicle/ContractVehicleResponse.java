package com.example.datn_qlnt_manager.dto.response.contractVehicle;

import com.example.datn_qlnt_manager.common.VehicleStatus;
import com.example.datn_qlnt_manager.common.VehicleType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractVehicleResponse {
    String id;
    String contractId;
    String tenantId;
    String vehicleId;
    VehicleType vehicleType;
    String licensePlate;
    String fullName;
    VehicleStatus vehicleStatus;
    Date registrationDate;
    LocalDate startDate;
    LocalDate endDate;
    String description;
    Instant createdAt;
    Instant updatedAt;
}
