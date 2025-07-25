package com.example.datn_qlnt_manager.dto.response.asset;

import com.example.datn_qlnt_manager.common.AssetGroup;

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
    AssetGroup assetGroup;
    String description;
}
