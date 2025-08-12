package com.example.datn_qlnt_manager.dto.filter;

import com.example.datn_qlnt_manager.common.AssetBeLongTo;
import com.example.datn_qlnt_manager.common.AssetStatus;
import com.example.datn_qlnt_manager.common.AssetType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssetFilter {
    String nameAsset;
    AssetType assetType;
    AssetBeLongTo assetBeLongTo;
    AssetStatus assetStatus;
    String buildingId;
}
