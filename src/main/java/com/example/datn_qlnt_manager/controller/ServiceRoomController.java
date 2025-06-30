package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.ServiceRoomFilter;
import com.example.datn_qlnt_manager.dto.request.serviceRoom.ServiceRoomCreationRequest;
import com.example.datn_qlnt_manager.dto.request.serviceRoom.ServiceRoomUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.serviceRoom.ServiceRoomResponse;
import com.example.datn_qlnt_manager.dto.statistics.ServiceRoomStatusStatistics;
import com.example.datn_qlnt_manager.service.ServiceRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("service-rooms")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "ServiceRoom", description = "API Service Room")
public class ServiceRoomController {

    ServiceRoomService serviceRoomService;

    @Operation(summary = "Thêm dịch vụ vào phòng")
    @PostMapping
    public ApiResponse<ServiceRoomResponse> createServiceRoom(@Valid @RequestBody ServiceRoomCreationRequest request) {
        return ApiResponse.<ServiceRoomResponse>builder()
                .message("Service room has been created!")
                .data(serviceRoomService.createServiceRoom(request))
                .build();
    }

    @Operation(summary = "Sửa dịch vụ phòng")
    @PutMapping("/{serviceRoomId}")
    public ApiResponse<ServiceRoomResponse> updateServiceRoom(@PathVariable("serviceRoomId") String serviceRoomId,@Valid @RequestBody ServiceRoomUpdateRequest request) {
        return ApiResponse.<ServiceRoomResponse>builder()
                .data(serviceRoomService.updateServiceRoom(serviceRoomId, request))
                .message("Service room has been updated!")
                .build();
    }

    @Operation(summary = "Xóa mềm (trạng thái: DA_HUY)")
    @PutMapping("/soft-delete/{serviceRoomId}")
    public ApiResponse<Void> softDeleteServiceRoom(@PathVariable("serviceRoomId") String serviceRoomId) {
        serviceRoomService.softDeleteServiceRoom(serviceRoomId);
        return ApiResponse.<Void>builder()
                .message("Service room deleted successfully.")
                .build();
    }

    @Operation(summary = "Xóa hoàn toàn")
    @DeleteMapping("/{roomServiceId}")
    public ApiResponse<String> deleteServiceRoom(@PathVariable("roomServiceId") String roomServiceId){
        serviceRoomService.deleteServiceRoom(roomServiceId);
        return ApiResponse.<String>builder()
                .message("Service room deleted successfully.")
                .build();
    }

    @Operation(summary = "Hiển thị, Tìm kiếm và lọc dịch vụ phòng theo người dùng hiện tại (có phân trang)")
    @GetMapping
    public ApiResponse<List<ServiceRoomResponse>> filterServiceRooms(
            @Valid @ModelAttribute ServiceRoomFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {

        PaginatedResponse<ServiceRoomResponse> result = serviceRoomService.filterServiceRooms(filter, page, size);

        return ApiResponse.<List<ServiceRoomResponse>>builder()
                .message("Filtered service rooms successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Thống kê dịch vụ phòng theo trạng thái (theo người dùng hiện tại)")
    @GetMapping("/statistics")
    public ApiResponse<ServiceRoomStatusStatistics> getStatisticsByStatus() {
        return ApiResponse.<ServiceRoomStatusStatistics>builder()
                .message("Statistics fetched successfully")
                .data(serviceRoomService.getServiceRoomStatusStatistics())
                .build();
    }
}
