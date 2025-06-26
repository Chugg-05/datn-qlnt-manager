package com.example.datn_qlnt_manager.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.AssetTypeFilter;
import com.example.datn_qlnt_manager.dto.request.assetType.AssetTypeCreationRequest;
import com.example.datn_qlnt_manager.dto.request.assetType.AssetTypeUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.assetType.AssetTypeResponse;
import com.example.datn_qlnt_manager.repository.AssetTypeRepository;
import com.example.datn_qlnt_manager.service.AssetTypeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@RequestMapping("/asset-types")
@Validated
@Tag(name = "AssetType", description = "API Asset Type")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssetTypeController {

    AssetTypeService assetTypeService;
    AssetTypeRepository assetTypeRepository;

    @Operation(summary = "Thêm loại tài sản mới")
    @PostMapping
    public ApiResponse<AssetTypeResponse> createAssetType(@Valid @RequestBody AssetTypeCreationRequest request) {
        return ApiResponse.<AssetTypeResponse>builder()
                .message("Asset type has been created!")
                .data(assetTypeService.createAssetType(request))
                .build();
    }

    @Operation(summary = "Hiển thị danh sách loại tài sản có phân trang, lọc (nhóm), tìm kiếm (tên)")
    @GetMapping
    public ApiResponse<PaginatedResponse<AssetTypeResponse>> getAssetTypes(
            @Valid @ModelAttribute AssetTypeFilter filter,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "15") @Min(1) int size) {
        return ApiResponse.<PaginatedResponse<AssetTypeResponse>>builder()
                .message("Asset type list loaded successfully")
                .data(assetTypeService.getAssetTypes(filter, page, size))
                .build();
    }

    @Operation(summary = "sửa loại tài sản")
    @PutMapping("/{assetTypeId}")
    public ApiResponse<AssetTypeResponse> updateAssetTypeId(
            @PathVariable String assetTypeId, @Valid @RequestBody AssetTypeUpdateRequest request) {
        return ApiResponse.<AssetTypeResponse>builder()
                .message("Asset type has been updated!")
                .data(assetTypeService.updateAssetType(assetTypeId, request))
                .build();
    }

    @Operation(summary = "Xóa hoàn toàn")
    @DeleteMapping("/{assetTypeId}")
    public ApiResponse<String> deleteAssetTypeId(@PathVariable String assetTypeId) {
        assetTypeService.deleteAssetTypeById(assetTypeId);
        return ApiResponse.<String>builder()
                .data("Asset type has been deleted!")
                .build();
    }
}
