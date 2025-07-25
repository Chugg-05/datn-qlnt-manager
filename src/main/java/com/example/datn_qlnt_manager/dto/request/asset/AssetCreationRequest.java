package com.example.datn_qlnt_manager.dto.request.asset;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.example.datn_qlnt_manager.common.AssetBeLongTo;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssetCreationRequest {
    @NotBlank(message = "ASSET_NAME_REQUIRED")
    String nameAsset;

    String assetTypeId;

    @NotNull(message = "ASSET_BELONG_TO_REQUIRED")
    AssetBeLongTo assetBeLongTo;

    String roomID;
    String buildingID;
    String floorID;
    String tenantId;

    @NotNull(message = "ASSET_PRICE_REQUIRED")
    @DecimalMin(value = "0.0", message = "ASSET_PRICE_INVALID")
    BigDecimal price;

    String descriptionAsset;
}
