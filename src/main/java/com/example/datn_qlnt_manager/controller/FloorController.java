package com.example.datn_qlnt_manager.controller;

import java.util.List;

import com.example.datn_qlnt_manager.configuration.Translator;
import jakarta.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.FloorFilter;
import com.example.datn_qlnt_manager.dto.request.floor.FloorCreationRequest;
import com.example.datn_qlnt_manager.dto.request.floor.FloorUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.IdAndName;
import com.example.datn_qlnt_manager.dto.response.floor.FloorBasicResponse;
import com.example.datn_qlnt_manager.dto.response.floor.FloorResponse;
import com.example.datn_qlnt_manager.dto.statistics.FloorRoomStatisticResponse;
import com.example.datn_qlnt_manager.dto.statistics.FloorStatistics;
import com.example.datn_qlnt_manager.service.FloorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/floors")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Floor", description = "API Floor")
public class FloorController {

    FloorService floorService;

    @Operation(summary = "Thêm tầng mới")
    @PostMapping
    public ApiResponse<FloorResponse> createFloor(@Valid @RequestBody FloorCreationRequest request) {
        return ApiResponse.<FloorResponse>builder()
                .message(Translator.toLocale("floor.create.success"))
                .data(floorService.createFloor(request))
                .build();
    }

    @Operation(summary = "Hiển hị, lọc (trạng thái tầng), tìm kiếm (tên tầng, số tầng tối đa)")
    @GetMapping
    public ApiResponse<List<FloorResponse>> getPageAndSearchAndFilterFloor(
            @Valid @ModelAttribute FloorFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {

        PaginatedResponse<FloorResponse> result =
                floorService.getPageAndSearchAndFilterFloorByUserId(filter, page, size);
        return ApiResponse.<List<FloorResponse>>builder()
                .message(Translator.toLocale("floor.retrieved.success"))
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Hiển hị, lọc (trạng thái tầng), tìm kiếm (tên tầng, số tầng tối đa) cho tầng đã hủy")
    @GetMapping("/cancel")
    public ApiResponse<List<FloorResponse>> getTenantWithStatusCancel(
            @Valid @ModelAttribute FloorFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {

        PaginatedResponse<FloorResponse> result = floorService.getTenantWithStatusCancelByUserId(filter, page, size);
        return ApiResponse.<List<FloorResponse>>builder()
                .message(Translator.toLocale("floor.cancel.retrieved.success"))
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Xóa mềm (chuyển trạng thái thành KHONG_SU_DUNG)")
    @PutMapping("/soft-delete/{floorId}")
    public ApiResponse<Void> softDeleteFloor(@PathVariable("floorId") String floorId) {
        floorService.softDeleteFloorById(floorId);
        return ApiResponse.<Void>builder()
                .message(Translator.toLocale("floor.soft.delete.success"))
                .build();
    }

    @Operation(summary = "Sửa thông tin tầng")
    @PutMapping("/{floorId}")
    public ApiResponse<FloorResponse> updateFloor(
            @PathVariable("floorId") String floorId, @Valid @RequestBody FloorUpdateRequest request) {
        return ApiResponse.<FloorResponse>builder()
                .message(Translator.toLocale("floor.update.success"))
                .data(floorService.updateFloor(floorId, request))
                .build();
    }

    @Operation(summary = "Xóa hoàn toàn")
    @DeleteMapping("/{floorId}")
    public ApiResponse<String> deleteFloor(@PathVariable("floorId") String floorId) {
        floorService.deleteFloor(floorId);
        return ApiResponse.<String>builder().data(Translator.toLocale("floor.delete.success")).build();
    }

    @Operation(summary = "Thống kê (tổng tầng, trạng thái: HOAT_DONG, KHONG_SU_DUNG)")
    @GetMapping("/floor-statistics")
    public ApiResponse<FloorStatistics> countFloorsByBuildingId(@RequestParam String buildingId) {
        return ApiResponse.<FloorStatistics>builder()
                .message(Translator.toLocale("floor.count.fetched.success"))
                .data(floorService.getFloorCountByBuildingId(buildingId))
                .build();
    }

    @Operation(summary = "Cập nhật trạng thái: hoạt động <-> tạm ngưng")
    @PutMapping("/toggle-status/{id}")
    public ApiResponse<String> toggleStatus(@PathVariable("id") String id) {
        floorService.toggleStatus(id);
        return ApiResponse.<String>builder()
                .message(Translator.toLocale("floor.status.update.success"))
                .build();
    }

    @Operation(summary = "Hiển thị danh sách tầng dạng card hoặc combobox theo building")
    @GetMapping("/find-all")
    public ApiResponse<List<FloorBasicResponse>> getFloorsByBuilding(@RequestParam String buildingId) {

        // Chỉ lọc theo buildingId, không cần userId
        List<FloorBasicResponse> response = floorService.getFloorBasicByBuildingId(buildingId);

        return ApiResponse.<List<FloorBasicResponse>>builder()
                .message(Translator.toLocale("floor.display.success"))
                .data(response)
                .build();
    }

    @Operation(summary = "Hiển thị danh sách tầng nhà theo người dùng")
    @GetMapping("/all")
    public ApiResponse<List<IdAndName>> getFloorsByUserId() {
        return ApiResponse.<List<IdAndName>>builder()
                .message(Translator.toLocale("floor.display.by.user.success"))
                .data(floorService.getFloorsByUserId())
                .build();
    }

    @GetMapping("/room-statistics/{floorId}")
    public ApiResponse<List<FloorRoomStatisticResponse>> getRoomStatisticTextByFloor(@PathVariable String floorId) {
        return ApiResponse.<List<FloorRoomStatisticResponse>>builder()
                .data(floorService.getRoomStatisticTextByFloor(floorId))
                .message(Translator.toLocale("floor.statistics.of.rooms.success"))
                .build();
    }

    @Operation(summary = "Khôi phục tầng đã xóa")
    @PutMapping("/restore/{floorId}")
    public ApiResponse<FloorResponse> restoreBuildingById(@PathVariable("floorId") String floorId) {
        return ApiResponse.<FloorResponse>builder()
                .data(floorService.restoreFloorById(floorId))
                .message(Translator.toLocale("floor.restore.success"))
                .build();
    }
}
