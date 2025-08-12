package com.example.datn_qlnt_manager.dto.response.asset;

import com.example.datn_qlnt_manager.common.AssetBeLongTo;
import com.example.datn_qlnt_manager.common.AssetStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssetLittleResponse {
    String id;
    String assetName;
    AssetBeLongTo assetBeLongTo;
    Integer quantity;
    BigDecimal price;
    AssetStatus assetStatus;
    String description;
}