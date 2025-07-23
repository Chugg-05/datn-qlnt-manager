package com.example.datn_qlnt_manager.controller;

import java.util.List;

import com.example.datn_qlnt_manager.dto.response.IdAndName;
import com.example.datn_qlnt_manager.dto.response.building.BuildingBasicResponse;
import com.example.datn_qlnt_manager.dto.response.building.BuildingOccupancyResponse;
import com.example.datn_qlnt_manager.dto.response.building.BuildingSelectResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.BuildingFilter;
import com.example.datn_qlnt_manager.dto.request.building.BuildingCreationRequest;
import com.example.datn_qlnt_manager.dto.request.building.BuildingUpdateRequest;
import com.example.datn_qlnt_manager.dto.statistics.BuildingStatistics;
import com.example.datn_qlnt_manager.dto.response.building.BuildingResponse;
import com.example.datn_qlnt_manager.service.BuildingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/buildings")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Building", description = "API Building")
public class BuildingController {
    BuildingService buildingService;

    @Operation(summary = "Phân trang, tìm kiếm, lọc tòa nhà")
    @GetMapping
    public ApiResponse<List<BuildingResponse>> getPageAndSearchAndFilterBuilding(
            @ModelAttribute BuildingFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        PaginatedResponse<BuildingResponse> result = buildingService.getPageAndSearchAndFilterBuildingByUserId(
                filter,
                page,
                size);

        return ApiResponse.<List<BuildingResponse>>builder()
                .message("Get building successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Phân trang, tìm kiếm, lọc tòa nhà đã hủy hoạt động (status = HUY_HOAT_DONG)")
    @GetMapping("/cancel")
    public ApiResponse<List<BuildingResponse>> getBuildingWithStatusCancel(
            @ModelAttribute BuildingFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        PaginatedResponse<BuildingResponse> result = buildingService.getBuildingWithStatusCancelByUserId(
                filter,
                page,
                size);

        return ApiResponse.<List<BuildingResponse>>builder()
                .message("Get cancelled buildings successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "hiển thị tòa nhà dạng card và hiển thị select box")
    @GetMapping("/cards")
    public ApiResponse<List<BuildingBasicResponse>> getBuildingCardsForCurrentUser() {
        return ApiResponse.<List<BuildingBasicResponse>>builder()
                .message("Display building as card successfully")
                .data(buildingService.getBuildingBasicForCurrentUser())
                .build();
    }

    @Operation(summary = "Thống kê tòa nhà theo trạng thái")
    @GetMapping("/statistics")
    public ApiResponse<BuildingStatistics> statisticsBuildingByStatus() {
        return ApiResponse.<BuildingStatistics>builder()
                .message("Count building success!")
                .data(buildingService.statisticsBuildingByStatus())
                .build();
    }

    @Operation(summary = "Thêm tòa nhà")
    @PostMapping
    public ApiResponse<BuildingResponse> createBuilding(@Valid @RequestBody BuildingCreationRequest request) {
        return ApiResponse.<BuildingResponse>builder()
                .message("Building has been created!")
                .data(buildingService.createBuilding(request))
                .build();
    }

    @Operation(summary = "Cập nhật tòa nhà")
    @PutMapping("/{buildingId}")
    public ApiResponse<BuildingResponse> updateBuilding(
            @Valid @RequestBody BuildingUpdateRequest request,
            @PathVariable("buildingId") String buildingId) {
        return ApiResponse.<BuildingResponse>builder()
                .message("Building updated!")
                .data(buildingService.updateBuilding(buildingId, request))
                .build();
    }

    @Operation(summary = "Cập nhật trạng thái: hoạt động <-> tạm ngưng")
    @PutMapping("/toggle-status/{id}")
    public ApiResponse<String> toggleStatus(@PathVariable("id") String id) {
        buildingService.toggleStatus(id);
        return ApiResponse.<String>builder()
                .message("Status update successful!")
                .build();
    }

    @Operation(summary = "Xóa tòa nhà (update trạng thái)")
    @PutMapping("/soft-delete/{buildingId}")
    public ApiResponse<String> softDeleteBuildingById(@PathVariable("buildingId") String buildingId) {
        buildingService.softDeleteBuildingById(buildingId);
        return ApiResponse.<String>builder().data("Building has been deleted!").build();
    }

    @Operation(summary = "Xóa tòa nhà")
    @DeleteMapping("/{buildingId}")
    public ApiResponse<String> deleteBuildingById(@PathVariable("buildingId") String buildingId) {
        buildingService.deleteBuildingById(buildingId);
        return ApiResponse.<String>builder().data("Building has been deleted!").build();
    }

    @Operation(summary = "Lấy thông tin tòa nhà - tầng - phòng")
    @GetMapping("/init")
    public ApiResponse<List<BuildingSelectResponse>> getBuildingsInfoByUserId() {
        List<BuildingSelectResponse> data = buildingService.getBuildingsInfoByUserId();
        return ApiResponse.<List<BuildingSelectResponse>>builder()
                .data(data)
                .message("Get buildings info successfully")
                .build();
    }

    @Operation(summary = "Lấy thông tin tòa nhà không phân trang")
    @GetMapping("/all")
    public ApiResponse<List<IdAndName>> getAllBuildingByUserId() {
        return ApiResponse.<List<IdAndName>>builder()
                .data(buildingService.getAllBuildingByUserId())
                .message("Get buildings successfully")
                .build();
    }

    @Operation(summary = "Tính tỉ lệ lấp đầy phòng trong các tòa nhà của người dùng hiện tại")
    @GetMapping("/occupancy-rate")
    public ApiResponse<List<BuildingOccupancyResponse>> getOccupancyRateByBuilding() {
        return ApiResponse.<List<BuildingOccupancyResponse>>builder()
                .data(buildingService.calculateOccupancyByUser())
                .message("Get occupancy rate successfully")
                .build();
    }
}