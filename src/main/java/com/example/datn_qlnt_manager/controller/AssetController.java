package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import java.util.List;

import com.example.datn_qlnt_manager.dto.response.asset.AssetResponse;
import jakarta.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.AssetFilter;
import com.example.datn_qlnt_manager.dto.request.asset.AssetCreationRequest;
import com.example.datn_qlnt_manager.dto.request.asset.AssetUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.asset.CreateAssetInit2Response;
import com.example.datn_qlnt_manager.dto.response.asset.CreateAssetInitResponse;
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
                .message("Asset has been created!")
                .data(assetService.createAsset(request))
                .build();
    }

    @Operation(summary = "Xóa hoàn toàn")
    @DeleteMapping("/{assetId}")
    public ApiResponse<String> deleteAssetById(@PathVariable String assetId) {
        assetService.deleteAssetById(assetId);
        return ApiResponse.<String>builder().data("Asset has been deleted!").build();
    }

    @Operation(summary = "Hiển thị danh sách tài sản có phân trang, lọc, tìm kiếm")
    @GetMapping
    public ApiResponse<PaginatedResponse<AssetResponse>> getPageAndSearchAndFilterAsset(
            @Valid @ModelAttribute AssetFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ApiResponse.<PaginatedResponse<AssetResponse>>builder()
                .message("Asset list loaded successfully")
                .data(assetService.getPageAndSearchAndFilterAssetByUserId(filter, page, size))
                .build();
    }

    @Operation(summary = "Sửa tài sản")
    @PutMapping("/{assetId}")
    public ApiResponse<AssetResponse> updateAsset(
            @PathVariable String assetId, @RequestBody @Valid AssetUpdateRequest request) {
        return ApiResponse.<AssetResponse>builder()
                .message("Asset has been updated!")
                .data(assetService.updateAssetById(assetId, request))
                .build();
    }

    @Operation(summary = "Hiển thị tài sản theo user đang đăng nhập")
    @GetMapping("/find-all")
    public ApiResponse<List<AssetResponse>> getAssetsByCurrentUser() {
        List<AssetResponse> data = assetService.findAssetsByCurrentUser();
        return ApiResponse.<List<AssetResponse>>builder()
                .data(data)
                .message("Asset has been found!")
                .build();
    }

    @Operation(summary = "Hiển thị thông tin liên quan để thêm mới tài sản theo người đang đăng nhập")
    @GetMapping("/init")
    public ApiResponse<CreateAssetInitResponse> getAssetsInfoByUserId() {
        CreateAssetInitResponse data = assetService.getInitDataForAssetCreation();
        return ApiResponse.<CreateAssetInitResponse>builder()
                .data(data)
                .message("Assets has been found!")
                .build();
    }

    @Operation(summary = "Hiển thị thông tin liên quan để thêm mới tài sản theo người đang đăng nhập")
    @GetMapping("/init/2")
    public ApiResponse<CreateAssetInit2Response> getAssetsInfoByUserId2() {
        CreateAssetInit2Response data = assetService.getAssetsInfoByUserId2();
        return ApiResponse.<CreateAssetInit2Response>builder()
                .data(data)
                .message("Assets has been found!")
                .build();
    }

}
