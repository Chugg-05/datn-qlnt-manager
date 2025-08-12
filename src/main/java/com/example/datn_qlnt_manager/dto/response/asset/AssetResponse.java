package com.example.datn_qlnt_manager.dto.response.asset;

import java.math.BigDecimal;

import com.example.datn_qlnt_manager.common.AssetBeLongTo;
import com.example.datn_qlnt_manager.common.AssetStatus;
import com.example.datn_qlnt_manager.common.AssetType;

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

    String buildingName;

    String nameAsset;

    AssetType assetType;

    AssetBeLongTo assetBeLongTo;

    AssetStatus assetStatus;

    BigDecimal price;

    Integer quantity;

    String descriptionAsset;

    String createdAt;

    String updatedAt;
}
