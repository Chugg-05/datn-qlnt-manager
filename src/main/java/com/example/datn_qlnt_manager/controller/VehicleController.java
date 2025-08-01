package com.example.datn_qlnt_manager.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.VehicleFilter;
import com.example.datn_qlnt_manager.dto.request.vehicle.VehicleCreationRequest;
import com.example.datn_qlnt_manager.dto.request.vehicle.VehicleUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.vehicle.VehicleResponse;
import com.example.datn_qlnt_manager.dto.statistics.VehicleStatistics;
import com.example.datn_qlnt_manager.service.VehicleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/vehicles")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Vehicle", description = "API Vehicle")
public class VehicleController {
    VehicleService vehicleService;

    @Operation(summary = "Phân trang, tìm kiếm, lọc phương tiện")
    @GetMapping
    public ApiResponse<List<VehicleResponse>> getPageAndSearchAndFilterVehicle(
            @ModelAttribute VehicleFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        PaginatedResponse<VehicleResponse> result =
                vehicleService.getPageAndSearchAndFilterVehicleByUserId(filter, page, size);

        return ApiResponse.<List<VehicleResponse>>builder()
                .message("Get vehicle successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Phân trang, tìm kiếm, lọc phương tiện đã hủy")
    @GetMapping("/cancel")
    public ApiResponse<List<VehicleResponse>> getVehicleWithStatusCancel(
            @ModelAttribute VehicleFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        PaginatedResponse<VehicleResponse> result =
                vehicleService.getVehicleWithStatusCancelByUserId(filter, page, size);

        return ApiResponse.<List<VehicleResponse>>builder()
                .message("Get vehicle with status cancel successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Xem tất cả phương tiện có trong phòng")
    @GetMapping("/{roomId}")
    public ApiResponse<List<VehicleResponse>> getVehiclesByRoomId (@PathVariable("roomId") String roomId) {
        return ApiResponse.<List<VehicleResponse>>builder()
                .data(vehicleService.getVehiclesByRoomId(roomId))
                .message("Get all vehicle by room successfully")
                .build();
    }

    @Operation(summary = "Thống kê phương tiện")
    @GetMapping("/statistics")
    public ApiResponse<VehicleStatistics> getVehicleStatistics() {
        return ApiResponse.<VehicleStatistics>builder()
                .message("Statistics vehicle success!")
                .data(vehicleService.getVehicleStatistics())
                .build();
    }

    @Operation(summary = "Thêm phương tiện")
    @PostMapping
    public ApiResponse<VehicleResponse> createVehicle(@Valid @RequestBody VehicleCreationRequest request) {
        return ApiResponse.<VehicleResponse>builder()
                .message("Vehicle has been created!")
                .data(vehicleService.createVehicle(request))
                .build();
    }

    @Operation(summary = "Cập nhật phương tiện")
    @PutMapping("/{vehicleId}")
    public ApiResponse<VehicleResponse> updateVehicle(
            @Valid @RequestBody VehicleUpdateRequest request, @PathVariable("vehicleId") String id) {
        return ApiResponse.<VehicleResponse>builder()
                .message("Vehicle updated!")
                .data(vehicleService.updateVehicle(id, request))
                .build();
    }

    @Operation(summary = "Xóa mềm phương tiện")
    @PutMapping("/soft-delete/{vehicleId}")
    public ApiResponse<String> softDeleteVehicleById(@PathVariable("vehicleId") String vehicleId) {
        vehicleService.softDeleteVehicleById(vehicleId);
        return ApiResponse.<String>builder()
                .message("Vehicle has been deleted!")
                .build();
    }

    @Operation(summary = "Xóa phương tiện")
    @DeleteMapping("/{vehicleId}")
    public ApiResponse<String> deleteVehicleById(@PathVariable("vehicleId") String vehicleId) {
        vehicleService.deleteVehicleById(vehicleId);
        return ApiResponse.<String>builder()
                .message("Vehicle has been deleted!")
                .build();
    }

    @Operation(summary = "Cập nhật trạng thái: hoạt động <-> tạm ngưng")
    @PutMapping("/toggle-status/{id}")
    public ApiResponse<String> toggleStatus(@PathVariable("id") String id) {
        vehicleService.toggleStatus(id);
        return ApiResponse.<String>builder()
                .message("Status update successful!")
                .build();
    }
}
