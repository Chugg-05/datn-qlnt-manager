package com.example.datn_qlnt_manager.dto.response.assetType;

import com.example.datn_qlnt_manager.common.AssetGroup;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssetTypeResponse {
     String id;
     String nameAssetType;
     AssetGroup assetGroup;
     String discriptionAssetType;
     Instant createdAt;
     Instant updatedAt;
}
