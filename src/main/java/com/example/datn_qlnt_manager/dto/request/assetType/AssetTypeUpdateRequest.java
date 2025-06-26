package com.example.datn_qlnt_manager.dto.request.assetType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.example.datn_qlnt_manager.common.AssetGroup;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssetTypeUpdateRequest {
    @NotBlank(message = "ASSET_TYPE_NAME_INVALID")
    String nameAssetType;

    @NotNull(message = "ASSET_GROUP_INVALID")
    AssetGroup assetGroup;

    @NotBlank(message = "DESCRIPTION_INVALID")
    String discriptionAssetType;
}
