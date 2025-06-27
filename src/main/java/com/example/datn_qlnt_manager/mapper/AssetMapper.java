package com.example.datn_qlnt_manager.mapper;

import com.example.datn_qlnt_manager.dto.request.asset.AssetCreationRequest;
import com.example.datn_qlnt_manager.dto.request.asset.AssetUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.asset.AssetResponse;
import com.example.datn_qlnt_manager.entity.Asset;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AssetMapper {
    @Mapping(target = "assetStatus",constant = "SU_DUNG")
    Asset toAsset(AssetCreationRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "assetTypeId", source = "assetType.id")
    @Mapping(target = "nameAssetType", source = "assetType.nameAssetType")

    @Mapping(target = "roomID", source = "room.id")
    @Mapping(target = "roomCode", source = "room.roomCode")

    @Mapping(target = "floorID", source = "floor.id")
    @Mapping(target = "nameFloor", source = "floor.nameFloor")

    @Mapping(target = "buildingID", source = "building.id")
    @Mapping(target = "buildingName", source = "building.buildingName")

    @Mapping(target = "tenantId", source = "tenant.id")
    @Mapping(target = "fullName", source = "tenant.fullName")
    AssetResponse toResponse(Asset asset);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateAsset(@MappingTarget Asset asset, AssetUpdateRequest request);

}
