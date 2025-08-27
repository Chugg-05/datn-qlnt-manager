package com.example.datn_qlnt_manager.controller;

import java.util.List;

import com.example.datn_qlnt_manager.configuration.Translator;
import jakarta.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.common.AssetStatus;
import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.AssetFilter;
import com.example.datn_qlnt_manager.dto.request.asset.AssetCreationRequest;
import com.example.datn_qlnt_manager.dto.request.asset.AssetUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.asset.AssetResponse;
import com.example.datn_qlnt_manager.dto.response.asset.CreateAssetInit2Response;
import com.example.datn_qlnt_manager.dto.response.asset.CreateAssetInitResponse;
import com.example.datn_qlnt_manager.dto.statistics.AssetStatusStatistic;
import com.example.datn_qlnt_manager.service.AssetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/assets")
@Tag(name = "Asset", description = "API Asset")
@Validated
public class AssetController {

    AssetService assetService;

    @Operation(summary = "Thêm tài sản mới")
    @PostMapping
    public ApiResponse<AssetResponse> createAsset(@Valid @RequestBody AssetCreationRequest request) {
        return ApiResponse.<AssetResponse>builder()
                .message(Translator.toLocale("asset.create.success"))
                .data(assetService.createAsset(request))
                .build();
    }

    @Operation(summary = "Xóa hoàn toàn")
    @DeleteMapping("/{assetId}")
    public ApiResponse<String> deleteAssetById(@PathVariable String assetId) {
        assetService.deleteAssetById(assetId);
        return ApiResponse.<String>builder().data(Translator.toLocale("asset.delete.success")).build();
    }

    @Operation(summary = "Hiển thị danh sách tài sản có phân trang, lọc, tìm kiếm")
    @GetMapping
    public ApiResponse<PaginatedResponse<AssetResponse>> getPageAndSearchAndFilterAsset(
            @Valid @ModelAttribute AssetFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ApiResponse.<PaginatedResponse<AssetResponse>>builder()
                .message(Translator.toLocale("asset.list.loaded.success"))
                .data(assetService.getPageAndSearchAndFilterAssetByUserId(filter, page, size))
                .build();
    }

    @Operation(summary = "Sửa tài sản")
    @PutMapping("/{assetId}")
    public ApiResponse<AssetResponse> updateAsset(
            @PathVariable String assetId, @RequestBody @Valid AssetUpdateRequest request) {
        return ApiResponse.<AssetResponse>builder()
                .message(Translator.toLocale("asset.update.success"))
                .data(assetService.updateAssetById(assetId, request))
                .build();
    }

    @Operation(summary = "Hiển thị tài sản theo id tòa nhà")
    @GetMapping("/find-all")
    public ApiResponse<List<AssetResponse>> getAssetsByBuildingId(@RequestParam String buildingId) {
        List<AssetResponse> data = assetService.findAssetsByBuildingId(buildingId);
        return ApiResponse.<List<AssetResponse>>builder()
                .data(data)
                .message(Translator.toLocale("asset.find.all.success"))
                .build();
    }

    @Operation(summary = "Hiển thị thông tin liên quan để thêm mới tài sản theo người đang đăng nhập")
    @GetMapping("/init")
    public ApiResponse<CreateAssetInitResponse> getAssetsInfoByUserId() {
        CreateAssetInitResponse data = assetService.getInitDataForAssetCreation();
        return ApiResponse.<CreateAssetInitResponse>builder()
                .data(data)
                .message(Translator.toLocale("asset.info.by.userid"))
                .build();
    }

    @Operation(summary = "Hiển thị thông tin liên quan để thêm mới tài sản theo người đang đăng nhập")
    @GetMapping("/init/2")
    public ApiResponse<CreateAssetInit2Response> getAssetsInfoByUserId2() {
        CreateAssetInit2Response data = assetService.getAssetsInfoByUserId2();
        return ApiResponse.<CreateAssetInit2Response>builder()
                .data(data)
                .message(Translator.toLocale("asset.info.by.userid"))
                .build();
    }

    @Operation(summary = "Thống kê tài sản theo trạng thái")
    @GetMapping("/statistics")
    public ApiResponse<AssetStatusStatistic> getAssetStatistics(@RequestParam String buildingId) {
        return ApiResponse.<AssetStatusStatistic>builder()
                .message(Translator.toLocale("asset.statistics.successfully"))
                .data(assetService.getAssetStatisticsByBuildingId(buildingId))
                .build();
    }

    @Operation(summary = "Xóa mềm tài sản (chuyển trạng thái KHONG_SU_DUNG)")
    @PutMapping("/soft-delete/{assetId}")
    public ApiResponse<Void> softDeleteAsset(@PathVariable String assetId) {
        assetService.softDeleteAsset(assetId);
        return ApiResponse.<Void>builder()
                .message(Translator.toLocale("asset.soft.delete.success"))
                .build();
    }

    @Operation(summary = "Chuyển trạng thái tài sản phòng HOAT_DONG <-> KHONG_SU_DUNG")
    @PutMapping("/toggle/{assetId}")
    public ApiResponse<String> updateAssetRoom(@PathVariable("assetId") String assetId) {

        assetService.toggleAsseStatus(assetId);

        return ApiResponse.<String>builder()
                .message(Translator.toLocale("asset.toggle.success"))
                .data("Asset with ID " + assetId + " has been toggled.")
                .build();
    }

    @Operation(summary = "Hiển thị toàn bộ tài sản (bỏ lọc tòa nhà)")
    @GetMapping("/find-all-no-buildingId")
    public ApiResponse<List<AssetResponse>> getAllAssets() {
        List<AssetResponse> data = assetService.findAllAssets();
        return ApiResponse.<List<AssetResponse>>builder()
                .data(data)
                .message(Translator.toLocale("get.all.assets"))
                .build();
    }

    @Operation(summary = "Khôi phục tài sản đã xóa")
    @PutMapping("/restore/{assetId}")
    public ApiResponse<AssetResponse> restoreBuildingById(@PathVariable("assetId") String assetId) {
        return ApiResponse.<AssetResponse>builder()
                .data( assetService.restoreAssetById(assetId))
                .message(Translator.toLocale("asset.has.been.restored"))
                .build();
    }

    @Operation(summary = "Phân trang, tìm kiếm, lọc tòa nhà đã hủy hoạt động (status = HUY)")
    @GetMapping("/cancel")
    public ApiResponse<PaginatedResponse<AssetResponse>> getBuildingWithStatusCancel(
            @Valid @ModelAttribute AssetFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        filter.setAssetStatus(AssetStatus.HUY);
        return ApiResponse.<PaginatedResponse<AssetResponse>>builder()
                .message(Translator.toLocale("asset.list.loaded.success"))
                .data(assetService.getPageAndSearchAndFilterAssetByUserIdAndCancel(filter, page, size))
                .build();
    }
}
