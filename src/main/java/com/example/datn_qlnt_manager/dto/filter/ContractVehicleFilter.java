package com.example.datn_qlnt_manager.dto.filter;

import com.example.datn_qlnt_manager.common.VehicleStatus;
import com.example.datn_qlnt_manager.common.VehicleType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractVehicleFilter {
    String query;
    VehicleType vehicleType;
    VehicleStatus vehicleStatus;
}
