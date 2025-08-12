package com.example.datn_qlnt_manager.dto.response.asset;

import com.example.datn_qlnt_manager.common.AssetStatus;
import com.example.datn_qlnt_manager.common.AssetType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssetBasicResponse {
    String id;
    String nameAsset;
    AssetType assetType;
    AssetStatus assetStatus;
    String description;
}
