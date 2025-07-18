package com.example.datn_qlnt_manager.dto.response.vehicle;

import com.example.datn_qlnt_manager.common.VehicleType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleBasicResponse {
    String id;
    VehicleType vehicleType;
    String licensePlate;
    String description;
}
