package com.example.datn_qlnt_manager.mapper;

import org.mapstruct.*;

import com.example.datn_qlnt_manager.dto.request.asset.AssetCreationRequest;
import com.example.datn_qlnt_manager.dto.request.asset.AssetUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.asset.AssetResponse;
import com.example.datn_qlnt_manager.entity.Asset;

@Mapper(componentModel = "spring")
public interface AssetMapper {

    Asset toAsset(AssetCreationRequest request);

    @Mapping(target = "id", source = "id")
    AssetResponse toResponse(Asset asset);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateAsset(@MappingTarget Asset asset, AssetUpdateRequest request);
}
