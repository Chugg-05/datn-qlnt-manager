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

    @Operation(summary = "Thêm tầng mới")
    @PostMapping
    public ApiResponse<FloorResponse> createFloor(@Valid @RequestBody FloorCreationRequest request) {
        return ApiResponse.<FloorResponse>builder()
                .message("Floor has been created!")
                .data(floorService.createFloor(request))
                .build();
    }

    @Operation(summary = "Hiển hị, lọc (trạng thái tầng), tìm kiếm (tên tầng, số tầng tối đa)")
    @GetMapping
    public ApiResponse<List<FloorResponse>> getFloors(
            @Valid @ModelAttribute FloorFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size

    ) {
        PaginatedResponse<FloorResponse> result = floorService.filterFloors(filter, page, size);
        return ApiResponse.<List<FloorResponse>>builder()
                .message("Floor data retrieved successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Xóa mềm (chuyển trạng thái thành KHONG_SU_DUNG)")
    @PutMapping("/soft-delete/{floorId}")
    public ApiResponse<Void> softDeleteFloor(@PathVariable("floorId") String floorId) {
        floorService.softDeleteFloorById(floorId);
        return ApiResponse.<Void>builder()
                .message("Floor deleted successfully.")
                .build();
    }

    @Operation(summary = "Sửa thông tin tầng")
    @PutMapping("/{floorId}")
    public ApiResponse<FloorResponse> updateFloor(
            @PathVariable("floorId") String floorId,
            @Valid @RequestBody FloorUpdateRequest request) {
        return ApiResponse.<FloorResponse>builder()
                .message("Floor has been updated!")
                .data(floorService.updateFloor(floorId, request))
                .build();
    }

    @Operation(summary = "Xóa hoàn toàn")
    @DeleteMapping("/{floorId}")
    public ApiResponse<String> deleteFloor(@PathVariable("floorId") String floorId) {
         floorService.deleteFloor(floorId);
         return ApiResponse.<String>builder()
                 .data("Floor deleted successfully.")
                 .build();

    }
}
