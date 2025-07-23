package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.BuildingFilter;
import com.example.datn_qlnt_manager.dto.request.building.BuildingCreationRequest;
import com.example.datn_qlnt_manager.dto.request.building.BuildingUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.IdAndName;
import com.example.datn_qlnt_manager.dto.response.building.BuildingBasicResponse;
import com.example.datn_qlnt_manager.dto.response.building.BuildingResponse;
import com.example.datn_qlnt_manager.dto.response.building.BuildingSelectResponse;
import com.example.datn_qlnt_manager.dto.statistics.BuildingStatistics;

import java.util.List;


public interface BuildingService {

    PaginatedResponse<BuildingResponse> getBuildingWithStatusCancelByUserId(
            BuildingFilter filter,
            int page,
            int size
    );

    List<BuildingBasicResponse> getBuildingBasicForCurrentUser();

    BuildingResponse createBuilding(BuildingCreationRequest request);

    BuildingResponse updateBuilding(String buildingId, BuildingUpdateRequest request);

    void softDeleteBuildingById(String buildingId);

    PaginatedResponse<BuildingResponse> getPageAndSearchAndFilterBuildingByUserId(BuildingFilter filter, int page, int size);

    void deleteBuildingById(String buildingId);

    BuildingStatistics statisticsBuildingByStatus();

    void toggleStatus(String id);

    List<BuildingSelectResponse>  getBuildingsInfoByUserId();

    List<IdAndName> getAllBuildingByUserId();
}
