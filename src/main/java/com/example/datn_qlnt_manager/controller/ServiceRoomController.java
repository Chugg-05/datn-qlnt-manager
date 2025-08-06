package com.example.datn_qlnt_manager.controller;

import java.util.List;

import com.example.datn_qlnt_manager.dto.response.IdNameAndType;
import com.example.datn_qlnt_manager.dto.projection.ServiceRoomView;
import com.example.datn_qlnt_manager.dto.request.service.ServiceUpdateUnitPriceRequest;
import com.example.datn_qlnt_manager.dto.request.serviceRoom.ServiceRoomCreationForBuildingRequest;
import com.example.datn_qlnt_manager.dto.request.serviceRoom.ServiceRoomCreationForServiceRequest;
import com.example.datn_qlnt_manager.dto.request.serviceRoom.ServiceRoomCreationRequest;
import com.example.datn_qlnt_manager.dto.response.service.ServiceDetailResponse;
import com.example.datn_qlnt_manager.dto.response.service.ServiceUpdateUnitPriceResponse;
import com.example.datn_qlnt_manager.dto.response.serviceRoom.ServiceRoomDetailResponse;
import com.example.datn_qlnt_manager.dto.response.serviceRoom.ServiceRoomResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.ServiceRoomFilter;
import com.example.datn_qlnt_manager.dto.request.serviceRoom.ServiceRoomCreationForRoomRequest;
import com.example.datn_qlnt_manager.dto.response.serviceRoom.CreateRoomServiceInitResponse;
import com.example.datn_qlnt_manager.dto.statistics.ServiceRoomStatistics;
import com.example.datn_qlnt_manager.service.ServiceRoomService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@RequestMapping("/service-rooms")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "ServiceRoom", description = "API Service Room")
public class ServiceRoomController {

    ServiceRoomService serviceRoomService;

    @Operation(summary = "Hiển thị, Tìm kiếm và lọc dịch vụ phòng theo người dùng hiện tại (có phân trang)")
    @GetMapping
    public ApiResponse<List<ServiceRoomView>> getServiceRoomsPaging(
            @Valid @ModelAttribute ServiceRoomFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {

        PaginatedResponse<ServiceRoomView> result = serviceRoomService.getServiceRoomsPaging(filter, page, size);

        return ApiResponse.<List<ServiceRoomView>>builder()
                .message("Service rooms fetched successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Thêm các dịch vụ vào phòng cho 1 phòng")
    @PostMapping("/by-room")
    public ApiResponse<ServiceRoomDetailResponse> createRoomServiceForRoom(
            @Valid @RequestBody ServiceRoomCreationForRoomRequest request
    ) {
        return ApiResponse.<ServiceRoomDetailResponse>builder()
                .message("Services added to the room!")
                .data(serviceRoomService.createRoomServiceForRoom(request))
                .build();
    }

    @Operation(summary = "Thêm 1 dịch vụ vào các phòng")
    @PostMapping("/by-service")
    public ApiResponse<ServiceDetailResponse> createRoomServiceForService(
            @Valid @RequestBody ServiceRoomCreationForServiceRequest request
    ) {
        return ApiResponse.<ServiceDetailResponse>builder()
                .message("This service has been added to the rooms!")
                .data(serviceRoomService.createRoomServiceForService(request))
                .build();
    }

    @Operation(summary = "Thêm 1 dịch vụ cho tất cả các phòng trong 1 tòa nhà")
    @PostMapping("/by-building")
    public ApiResponse<ServiceDetailResponse> createRoomServiceForBuilding(
            @Valid @RequestBody ServiceRoomCreationForBuildingRequest request
    ) {
        return ApiResponse.<ServiceDetailResponse>builder()
                .message("This service has been added to the building.!")
                .data(serviceRoomService.createRoomServiceForBuilding(request))
                .build();
    }

    @Operation(summary = "Thêm 1 dịch vụ cho 1 phòng")
    @PostMapping
    public ApiResponse<ServiceRoomResponse> createRoomService(
            @Valid @RequestBody ServiceRoomCreationRequest request
    ) {
        return ApiResponse.<ServiceRoomResponse>builder()
                .message("Service room has been created!")
                .data(serviceRoomService.createServiceRoom(request))
                .build();
    }

    @Operation(summary = "Cập nhật giá dịch vụ trong tòa nhà")
    @PutMapping("/serviceId/{serviceId}/buildingId/{buildingId}")
    public ApiResponse<ServiceUpdateUnitPriceResponse> updateServicePriceInBuilding(
            @PathVariable String buildingId,
            @PathVariable String serviceId,
            @RequestBody @Valid ServiceUpdateUnitPriceRequest request) {

        request.setBuildingId(buildingId);
        request.setServiceId(serviceId);

        ServiceUpdateUnitPriceResponse response = serviceRoomService.updateServicePriceInBuilding(request);

        return ApiResponse.<ServiceUpdateUnitPriceResponse>builder()
                .message("Service price updated successfully!")
                .data(response)
                .build();
    }

    @Operation(summary = "Xem thông tin các dịch vụ có trong phòng")
    @GetMapping("/{roomId}")
    public ApiResponse<ServiceRoomDetailResponse> getServiceRoomDetail(@PathVariable("roomId") String roomId) {
        return ApiResponse.<ServiceRoomDetailResponse>builder()
                .message("Service room details fetched successfully")
                .data(serviceRoomService.getServiceRoomDetail(roomId))
                .build();
    }

    @Operation(summary = "Xóa hoàn toàn")
    @DeleteMapping("/{roomServiceId}")
    public ApiResponse<String> deleteServiceRoom(@PathVariable("roomServiceId") String roomServiceId) {
        serviceRoomService.deleteServiceRoom(roomServiceId);
        return ApiResponse.<String>builder()
                .message("Service room deleted successfully.")
                .build();
    }

    @Operation(summary = "Thống kê dịch vụ phòng theo trạng thái (theo người dùng hiện tại)")
    @GetMapping("/statistics/{buildingId}")
    public ApiResponse<ServiceRoomStatistics> getStatisticsByStatus(@PathVariable(name = "buildingId") String buildingId) {
        return ApiResponse.<ServiceRoomStatistics>builder()
                .message("Statistics fetched successfully")
                .data(serviceRoomService.getServiceRoomStatusStatistics(buildingId))
                .build();
    }

    @Operation(summary = "Cập nhật trạng thái: dang su dung <-> tam dung")
    @PutMapping("/toggle-status/{serviceRoomId}")
    public ApiResponse<String> toggleServiceRoomStatus(@PathVariable("serviceRoomId") String serviceRoomId) {
        serviceRoomService.toggleServiceRoomStatus(serviceRoomId);
        return ApiResponse.<String>builder()
                .message("Service room status update successful!")
                .build();
    }

    @Operation(summary = "Hiển thị thông tin liên quan để thêm mới và cập nhật tài sản phòng theo người đang đăng nhập")
    @GetMapping("/init")
    public ApiResponse<CreateRoomServiceInitResponse> getServiceRoomInfoByUserId() {
        CreateRoomServiceInitResponse data = serviceRoomService.getServiceRoomInfoByUserId();
        return ApiResponse.<CreateRoomServiceInitResponse>builder()
                .data(data)
                .message(" has been found!")
                .build();
    }

    @Operation(summary = "Hiển thị dịch vụ phòng theo phòng và chủ trọ đang đăng nhập")
    @GetMapping("/all/{roomId}")
    public ApiResponse<List<IdNameAndType>> getAllServiceRoomByUserId(@PathVariable(name = "roomId") String roomId) {
        return ApiResponse.<List<IdNameAndType>>builder()
                .data(serviceRoomService.getAllServiceRoomByUserId(roomId))
                .message("Service room has been found!")
                .build();
    }
}
