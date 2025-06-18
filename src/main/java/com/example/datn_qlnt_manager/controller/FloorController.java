package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.FloorFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.request.floor.FloorCreationRequest;
import com.example.datn_qlnt_manager.dto.request.floor.FloorUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.floor.FloorResponse;
import com.example.datn_qlnt_manager.service.FloorService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@RestController
@RequestMapping("/floors")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Floor", description = "API Floor")
public class FloorController {

    FloorService floorService;

    @Operation(summary = "thêm tầng mới")
    @PostMapping
    public ApiResponse<FloorResponse> createFloor(@Valid @RequestBody FloorCreationRequest request) {
        return floorService.createFloor(request);
    }

    @Operation(summary = "Hiển hị, lọc(trạng thái tầng), tìm kiếm (tên tầng, số tầng tối đa)")
    @GetMapping
    public ApiResponse<List<FloorResponse>> getFloors(
            @Valid @ModelAttribute FloorFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PaginatedResponse<FloorResponse> result = floorService.filterFloors(filter, page, size);
        return ApiResponse.<List<FloorResponse>>builder()
                .message("Floor data retrieved successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Xóa mềm(trạng thái của tầng về KHONG_SU_DUNG)")
    @DeleteMapping("/soft-delete/{id}")
    public ApiResponse<Void> softDeleteFloor(@PathVariable("id") String floorId) {
        floorService.softDeleteFloorById(floorId);
        return ApiResponse.<Void>builder()
                .message("Floor deleted successfully.")
                .build();
    }

    @Operation(summary = "Sửa thông tin tầng")
    @PutMapping("/{id}")
    public ApiResponse<FloorResponse> updateFloor(
            @PathVariable("id") String id,
            @Valid @RequestBody FloorUpdateRequest request) {
        return floorService.updateFloor(id, request);
    }

    @Operation(summary = "Xóa tầng (Mất dữ liệu luôn từ DB)")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteFloor(@PathVariable("id") String floorId) {
        return floorService.deleteFloor(floorId);
    }
}
