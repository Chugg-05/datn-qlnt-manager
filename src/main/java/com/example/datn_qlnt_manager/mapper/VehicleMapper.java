package com.example.datn_qlnt_manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.datn_qlnt_manager.dto.request.vehicle.VehicleCreationRequest;
import com.example.datn_qlnt_manager.dto.request.vehicle.VehicleUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.vehicle.VehicleResponse;
import com.example.datn_qlnt_manager.entity.Vehicle;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    Vehicle toVehicle(VehicleCreationRequest request);

    @Mapping(source = "tenant.fullName", target = "fullName")
    VehicleResponse toVehicleResponse(Vehicle vehicle);

    void updateVehicle(@MappingTarget Vehicle vehicle, VehicleUpdateRequest request);
}
