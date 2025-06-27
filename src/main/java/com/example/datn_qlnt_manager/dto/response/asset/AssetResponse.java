package com.example.datn_qlnt_manager.dto.response.asset;

import com.example.datn_qlnt_manager.common.AssetBeLongTo;
import com.example.datn_qlnt_manager.common.AssetStatus;
import java.math.BigDecimal;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssetResponse {
    String id;

    String nameAsset;

    String assetTypeId;
    String nameAssetType;

    AssetBeLongTo assetBeLongTo;

    String roomID;
    String roomCode;

    String buildingID;
    String buildingName;

    String floorID;
    String nameFloor;

    String tenantId;
    String fullName;

    BigDecimal price;

    AssetStatus assetStatus;

    String descriptionAsset;

    String createdAt;

    String updatedAt;
}