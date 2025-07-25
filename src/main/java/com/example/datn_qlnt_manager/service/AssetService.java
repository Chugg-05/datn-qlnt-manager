package com.example.datn_qlnt_manager.service;

import java.util.List;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.AssetFilter;
import com.example.datn_qlnt_manager.dto.request.asset.AssetCreationRequest;
import com.example.datn_qlnt_manager.dto.request.asset.AssetUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.asset.AssetResponse;
import com.example.datn_qlnt_manager.dto.response.asset.CreateAssetInitResponse;

public interface AssetService {
    AssetResponse createAsset(AssetCreationRequest request);

    void deleteAssetById(String assetId);

    PaginatedResponse<AssetResponse> getPageAndSearchAndFilterAssetByUserId(AssetFilter filter, int page, int size);

    AssetResponse updateAssetById(String assetId, AssetUpdateRequest request);

    List<AssetResponse> findAssetsByCurrentUser();

    CreateAssetInitResponse getInitDataForAssetCreation();
}
