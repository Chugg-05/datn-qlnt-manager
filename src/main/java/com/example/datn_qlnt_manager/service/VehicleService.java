package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.VehicleFilter;
import com.example.datn_qlnt_manager.dto.request.vehicle.VehicleCreationRequest;
import com.example.datn_qlnt_manager.dto.request.vehicle.VehicleUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.vehicle.VehicleResponse;
import com.example.datn_qlnt_manager.dto.statistics.VehicleStatistics;

public interface VehicleService {
    PaginatedResponse<VehicleResponse> filterVehicles(VehicleFilter filter, int page, int size);

    VehicleResponse createVehicle(VehicleCreationRequest request);

    VehicleResponse updateVehicle(String vehicleId, VehicleUpdateRequest request);

    void softDeleteVehicleById(String vehicleId);

    void deleteVehicleById(String vehicleId);

    VehicleStatistics getVehicleStatistics();
}
