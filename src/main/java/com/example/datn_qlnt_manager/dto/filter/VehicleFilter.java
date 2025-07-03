package com.example.datn_qlnt_manager.dto.filter;

import com.example.datn_qlnt_manager.common.VehicleType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleFilter {
    VehicleType vehicleType;
    String licensePlate;
    String userId;
}
