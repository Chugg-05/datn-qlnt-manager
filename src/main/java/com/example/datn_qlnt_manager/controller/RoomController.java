package com.example.datn_qlnt_manager.controller;

import java.util.List;

import com.example.datn_qlnt_manager.configuration.Translator;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.RoomFilter;
import com.example.datn_qlnt_manager.dto.request.room.RoomCreationRequest;
import com.example.datn_qlnt_manager.dto.request.room.RoomUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.room.RoomCountResponse;
import com.example.datn_qlnt_manager.dto.response.room.RoomDetailsResponse;
import com.example.datn_qlnt_manager.dto.response.room.RoomResponse;
import com.example.datn_qlnt_manager.dto.statistics.RoomNoServiceStatisticResponse;
import com.example.datn_qlnt_manager.dto.statistics.RoomStatisticWithoutAssets;
import com.example.datn_qlnt_manager.dto.statistics.StatisticRoomsWithoutContract;
import com.example.datn_qlnt_manager.service.RoomService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Room", description = "API Room")
public class RoomController {

    RoomService roomService;

    @Operation(summary = "Phân trang, tìm kiếm, lọc phòng")
    @GetMapping
    public ApiResponse<List<RoomResponse>> getPageAndSearchAndFilterRoom(
            @ModelAttribute RoomFilter roomFilter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        PaginatedResponse<RoomResponse> result =
                roomService.getPageAndSearchAndFilterRoomByUserId(roomFilter, page, size);

        return ApiResponse.<List<RoomResponse>>builder()
                .message(Translator.toLocale("get.rooms.success"))
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Phân trang, tìm kiếm, lọc phòng dã hủy")
    @GetMapping("/cancel")
    public ApiResponse<List<RoomResponse>> getRoomWithStatusCancel(
            @ModelAttribute RoomFilter roomFilter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        PaginatedResponse<RoomResponse> result = roomService.getRoomWithStatusCancelByUserId(roomFilter, page, size);

        return ApiResponse.<List<RoomResponse>>builder()
                .message(Translator.toLocale("get.rooms.with.status.cancel.success"))
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Phân trang, lọc(theo tòa), hiển thị danh sách phòng chưa có dịch vụ")
    @GetMapping("/without-services")
    public ApiResponse<List<RoomResponse>> getRoomsWithoutService(
            @ModelAttribute RoomFilter roomFilter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        PaginatedResponse<RoomResponse> result = roomService.getRoomsWithoutServiceByUserId(roomFilter, page, size);
        return ApiResponse.<List<RoomResponse>>builder()
                .message(Translator.toLocale("get.rooms.without.service.success"))
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "hiển thị danh sách phòng theo id khách thuê")
    @GetMapping("/by-tenant")
    public ApiResponse<List<RoomResponse>> getRoomsByTenant() {
        return ApiResponse.<List<RoomResponse>>builder()
                .data(roomService.getRoomsByTenantId())
                .message(Translator.toLocale("get.all.room.list.by.tenant.success"))
                .build();
    }

    @Operation(summary = "hiển thị danh sách phòng chưa có tài sản")
    @GetMapping("/rooms-without-assets")
    public ApiResponse<List<RoomResponse>> getRoomsWithoutAssets(
            @RequestParam String buildingId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        PaginatedResponse<RoomResponse> result = roomService.getRoomsWithoutAssets(buildingId, page, size);
        return ApiResponse.<List<RoomResponse>>builder()
                .message(Translator.toLocale("get.rooms.without.asset.success"))
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @GetMapping("/statistics")
    public ApiResponse<RoomCountResponse> statisticsRoomByStatus(@RequestParam String buildingId) {
        return ApiResponse.<RoomCountResponse>builder()
                .message(Translator.toLocale("count.room.success"))
                .data(roomService.statisticsRoomByStatus(buildingId))
                .build();
    }

    @Operation(summary = "Thống kê số phòng chưa có hợp đồng")
    @GetMapping("/statistic-without-contract")
    public ApiResponse<StatisticRoomsWithoutContract> statisticRoomsWithoutContract() {
        return ApiResponse.<StatisticRoomsWithoutContract>builder()
                .message(Translator.toLocale("statistic.rooms.without.contract.success"))
                .data(roomService.statisticRoomsWithoutContractByUserId())
                .build();
    }

    @Operation(summary = "thống kê số phòng chưa có tài sản")
    @GetMapping("/statistic-without-asset")
    public ApiResponse<RoomStatisticWithoutAssets> statisticRoomsWithoutAsset(@RequestParam String buildingId) {
        return ApiResponse.<RoomStatisticWithoutAssets>builder()
                .message(Translator.toLocale("statistic.rooms.without.asset.success"))
                .data(roomService.statisticRoomsWithoutAssetByUserId(buildingId))
                .build();
    }

    @Operation(summary = "Tạo phòng mới")
    @PostMapping
    public ApiResponse<RoomResponse> createRoom(@RequestBody @Valid RoomCreationRequest request) {
        return ApiResponse.<RoomResponse>builder()
                .data(roomService.createRoom(request))
                .message(Translator.toLocale("room.create.success"))
                .build();
    }

    @PutMapping("/update/{id}")
    public ApiResponse<RoomResponse> updateRoom(
            @PathVariable("id") String roomId, @RequestBody @Valid RoomUpdateRequest request) {
        return ApiResponse.<RoomResponse>builder()
                .data(roomService.updateRoom(roomId, request))
                .message(Translator.toLocale("room.update.success"))
                .code(200)
                .build();
    }

    @Operation(summary = "Lấy ds phòng theo userId")
    @GetMapping("/all")
    public ApiResponse<List<RoomResponse>> getAllRooms() {
        return ApiResponse.<List<RoomResponse>>builder()
                .data(roomService.getAllRoomsByUserId())
                .message(Translator.toLocale("get.all.rooms.success"))
                .code(200)
                .build();
    }
    @Operation(summary = "Hiển thị phòng theo id tòa nhà")
    @GetMapping("/find-all")
    public ApiResponse<List<RoomResponse>> getRoomsByBuildingId(@RequestParam String buildingId) {
        List<RoomResponse> data = roomService.findRoomsByBuildingId(buildingId);
        return ApiResponse.<List<RoomResponse>>builder()
                .data(data)
                .message(Translator.toLocale("rooms.have.been.found"))
                .code(200)
                .build();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteRoom(@PathVariable("id") String roomId) {
        return ApiResponse.<Void>builder()
                .data(roomService.deleteRoom(roomId))
                .message(Translator.toLocale("room.delete.success"))
                .code(200)
                .build();
    }

    @PutMapping("/soft-delete/{id}")
    public ApiResponse<Void> softDeleteRoom(@PathVariable("id") String id) {
        roomService.softDeleteRoomById(id);
        return ApiResponse.<Void>builder()
                .message(Translator.toLocale("room.delete.success"))
                .code(200)
                .build();
    }

    @Operation(summary = "Thống kê phòng chưa có dịch vụ")
    @GetMapping("/statistic/no-service")
    public ApiResponse<List<RoomNoServiceStatisticResponse>> getRoomNoServiceStatistic(
            @RequestParam(required = false) String buildingId) {
        List<RoomNoServiceStatisticResponse> data = roomService.getRoomNoServiceStatistic(buildingId);

        return ApiResponse.<List<RoomNoServiceStatisticResponse>>builder()
                .message(Translator.toLocale("statistics.of.rooms.with.no.service.successful"))
                .data(data)
                .build();
    }

    @Operation(summary = "Xem chi tiết thông tin phòng")
    @GetMapping("/details/{roomId}")
    public ApiResponse<RoomDetailsResponse> getRoomDetails(@PathVariable String roomId) {
        RoomDetailsResponse response = roomService.getRoomDetails(roomId);
        return ApiResponse.<RoomDetailsResponse>builder()
                .message(Translator.toLocale("get.room.details.successfully"))
                .data(response)
                .build();
    }

    @Operation(summary = "Khôi phục phòng đã xóa")
    @PutMapping("/restore/{roomId}")
    public ApiResponse<RoomResponse> restoreRoomById(@PathVariable("roomId") String roomId) {
        return ApiResponse.<RoomResponse>builder()
                .data(roomService.restoreRoomById(roomId))
                .message(Translator.toLocale("room.has.been.restored"))
                .build();
    }
}
