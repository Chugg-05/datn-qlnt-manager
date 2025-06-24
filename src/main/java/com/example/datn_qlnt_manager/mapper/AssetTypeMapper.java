package com.example.datn_qlnt_manager.mapper;

import com.example.datn_qlnt_manager.dto.request.assetType.AssetTypeCreationRequest;
import com.example.datn_qlnt_manager.dto.request.assetType.AssetTypeUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.assetType.AssetTypeResponse;
import com.example.datn_qlnt_manager.entity.AssetType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface AssetTypeMapper {
    AssetType toAssetType(AssetTypeCreationRequest request);

    AssetTypeResponse toResponse(AssetType assetType);

    @Mapping(target = "id", ignore = true)
    void updateAssetType(AssetTypeUpdateRequest request, @MappingTarget AssetType assetType);
}
