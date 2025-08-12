package com.example.datn_qlnt_manager.service;

import org.springframework.transaction.annotation.Transactional;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.AssetRoomFilter;
import com.example.datn_qlnt_manager.dto.projection.AssetRoomView;
import com.example.datn_qlnt_manager.dto.request.assetRoom.*;
import com.example.datn_qlnt_manager.dto.response.asset.AssetDetailResponse;
import com.example.datn_qlnt_manager.dto.response.assetRoom.AssetRoomDetailResponse;
import com.example.datn_qlnt_manager.dto.response.assetRoom.AssetRoomResponse;
import com.example.datn_qlnt_manager.dto.statistics.AssetStatusStatistic;

import io.swagger.v3.oas.annotations.Operation;

public interface AssetRoomService {
    PaginatedResponse<AssetRoomView> getAssetRoomsPaging(AssetRoomFilter filter, int page, int size);

    @Operation(summary = "Get detail of Asset Room by ID")
    AssetRoomDetailResponse getAssetRoomDetail(String roomId);

    AssetRoomResponse createAssetRoom(AssetRoomCreationRequest request);

    AssetDetailResponse createAssetRoomForBuilding(AssetRoomCreationForBuildingRequest request);

    @Transactional
    AssetDetailResponse createAssetRoomForAsset(AssetRoomCreationForAssetRequest request);

    @Transactional
    AssetRoomDetailResponse createAssetRoomForRoom(AssetRoomCreationForRoomRequest request);

    AssetRoomResponse updateAssetRoom(String assetRoomId, AssetRoomUpdateRequest request);

    AssetStatusStatistic getAssetStatisticsByBuildingId(String buildingId);

    void toggleAssetRoomStatus(String assetRoomId);

    void deleteAssetRoom(String assetRoomId);
}
