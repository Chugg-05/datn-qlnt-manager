package com.example.datn_qlnt_manager.dto.response.assetType;

import com.example.datn_qlnt_manager.common.AssetGroup;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssetTypeBasicResponse {
    String id;
    String nameAssetType;
    AssetGroup assetGroup;
    String descriptionAssetType;
}
