package com.example.datn_qlnt_manager.service;

import java.util.List;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.AssetTypeFilter;
import com.example.datn_qlnt_manager.dto.request.assetType.AssetTypeCreationRequest;
import com.example.datn_qlnt_manager.dto.request.assetType.AssetTypeUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.assetType.AssetTypeResponse;

public interface AssetTypeService {
    AssetTypeResponse createAssetType(AssetTypeCreationRequest request);

    PaginatedResponse<AssetTypeResponse> getPageAndSearchAndFilterAssetTypeByUserId(
            AssetTypeFilter filter, int page, int size);

    AssetTypeResponse updateAssetType(String assetTypeId, AssetTypeUpdateRequest request);

    List<AssetTypeResponse> getAllAssetTypesByUserId();

    void deleteAssetTypeById(String assetTypeId);

    List<AssetTypeResponse> findAssetTypesByCurrentUser();
}
