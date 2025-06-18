package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.BuildingFilter;
import com.example.datn_qlnt_manager.dto.request.building.BuildingCreateRequest;
import com.example.datn_qlnt_manager.dto.request.building.BuildingUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.building.BuildingResponse;

public interface BuildingService {
    BuildingResponse createBuilding(BuildingCreateRequest request);

    BuildingResponse updateBuilding(String buildingId, BuildingUpdateRequest request);

    void softDeleteBuildingById(String buildingId);

    PaginatedResponse<BuildingResponse> filterBuildings(BuildingFilter filter, int page, int size);

    void deleteBuildingById(String buildingId);
}
