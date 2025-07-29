package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.AssetRoomFilter;
import com.example.datn_qlnt_manager.dto.projection.AssetRoomView;
import com.example.datn_qlnt_manager.dto.request.assetRoom.*;
import com.example.datn_qlnt_manager.dto.response.asset.AssetDetailResponse;
import com.example.datn_qlnt_manager.dto.response.assetRoom.AssetRoomDetailResponse;
import com.example.datn_qlnt_manager.dto.response.assetRoom.AssetRoomResponse;
import com.example.datn_qlnt_manager.dto.statistics.AssetStatusStatistic;
import com.example.datn_qlnt_manager.service.AssetRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/asset-rooms")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Asset Room", description = "API Asset Room")
public class AssetRoomController {
    AssetRoomService assetRoomService;

    @Operation(summary = "Hiển thị, Tìm kiếm và lọc tài sản phòng theo người dùng hiện tại (có phân trang)")
    @GetMapping
    public ApiResponse<List<AssetRoomView>> getAssetRoomsPaging(
            @Valid @ModelAttribute AssetRoomFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {

        PaginatedResponse<AssetRoomView> result = assetRoomService.getAssetRoomsPaging(filter, page, size);

        return ApiResponse.<List<AssetRoomView>>builder()
                .message("Service rooms fetched successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Xem thông tin các tài sản có trong phòng")
    @GetMapping("/{roomId}")
    public ApiResponse<AssetRoomDetailResponse> getAssetRoomDetail(@PathVariable("roomId") String roomId) {
        return ApiResponse.<AssetRoomDetailResponse>builder()
                .message("Asset room details fetched successfully")
                .data(assetRoomService.getAssetRoomDetail(roomId))
                .build();
    }

    @Operation(summary = "Create Asset Room for Service")
    @PostMapping
    public ApiResponse<AssetRoomResponse> createAssetRoom(
           @Valid @RequestBody AssetRoomCreationRequest request
    ) {
        return ApiResponse.<AssetRoomResponse>builder()
                .message("Asset Room created successfully")
                .data(assetRoomService.createAssetRoom(request))
                .build();
    }

    @Operation(summary = "Thêm 1 tài sản cho tất cả các phòng trong 1 tòa nhà")
    @PostMapping("/by-building")
    public ApiResponse<AssetDetailResponse> createRoomAssetForBuilding(
            @Valid @RequestBody AssetRoomCreationForBuildingRequest request
    ) {
        return ApiResponse.<AssetDetailResponse>builder()
                .message("This asset has been added to the building.!")
                .data(assetRoomService.createAssetRoomForBuilding(request))
                .build();
    }

    @Operation(summary = "Thêm 1 tài sản vào các phòng")
    @PostMapping("/by-asset")
    public ApiResponse<AssetDetailResponse> createRoomServiceForService(
            @Valid @RequestBody AssetRoomCreationForAssetRequest request
    ) {
        return ApiResponse.<AssetDetailResponse>builder()
                .message("This asset has been added to the rooms!")
                .data(assetRoomService.createAssetRoomForAsset(request))
                .build();
    }

    @Operation(summary = "Thêm 1 tài sản vào các phòng")
    @PostMapping("/by-room")
    public ApiResponse<AssetRoomDetailResponse> createAssetRoomForRoom(
            @Valid @RequestBody AssetRoomCreationForRoomRequest request
    ) {
        return ApiResponse.<AssetRoomDetailResponse>builder()
                .message("Assets added to the room!")
                .data(assetRoomService.createAssetRoomForRoom(request))
                .build();
    }

    @Operation(summary = "Cập nhật thông tin tài sản trong phòng")
    @PutMapping("/{assetRoomId}")
    public ApiResponse<AssetRoomResponse > updateAssetRoom(
            @PathVariable("assetRoomId") String assetRoomId,
            @RequestBody @Valid AssetRoomUpdateRequest request) {
        AssetRoomResponse  response = assetRoomService.updateAssetRoom(assetRoomId, request);

        return ApiResponse.<AssetRoomResponse>builder()
                .message("Asset room updated successfully")
                .data(response)
                .build();
    }

    @Operation(summary = "Thống kê số trạng thái tài sản theo tòa")
    @GetMapping("/statistics/{buildingId}")
    public ApiResponse<AssetStatusStatistic> getAssetStatistics(@PathVariable("buildingId") String buildingId) {

        return ApiResponse.<AssetStatusStatistic>builder()
                .message("Asset statistics successfully")
                .data(assetRoomService.getAssetStatisticsByBuildingId(buildingId))
                .build();
    }

    @Operation(summary = "Chuyển trạng thái tài sản phòng HOAT_DONG <-> HU_HONG")
    @PutMapping("/toggle/{assetRoomId}")
    public ApiResponse<String> updateAssetRoom(
            @PathVariable("assetRoomId") String assetRoomId) {

        assetRoomService.toggleAssetRoomStatus(assetRoomId);

        return ApiResponse.<String>builder()
                .message("Asset room toggle successfully")
                .data("Asset room with ID " + assetRoomId + " has been toggled.")
                .build();

    }

    @Operation(summary = "Xóa tài sản trong phòng")
    @DeleteMapping("/{assetRoomId}")
    public ApiResponse<String> deleteAssetRoom(@PathVariable("assetRoomId") String assetRoomId) {

        assetRoomService.deleteAssetRoom(assetRoomId);

        return ApiResponse.<String>builder()
                .message("Asset room deleted successfully")
                .data("Asset room with ID " + assetRoomId + " has been deleted.")
                .build();

    }

}
