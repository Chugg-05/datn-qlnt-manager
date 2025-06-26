package com.example.datn_qlnt_manager.dto.response.vehicle;

import com.example.datn_qlnt_manager.common.VehicleStatus;
import com.example.datn_qlnt_manager.common.VehicleType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleResponse {
    String fullName;
    VehicleType vehicleType;
    String licensePlate;
    VehicleStatus vehicleStatus;
    Date registrationDate;
    String describe;
    Instant createdAt;
    Instant updatedAt;
}
