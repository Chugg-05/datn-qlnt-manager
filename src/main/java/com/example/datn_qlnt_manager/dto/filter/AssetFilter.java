package com.example.datn_qlnt_manager.dto.filter;

import com.example.datn_qlnt_manager.common.AssetBeLongTo;
import com.example.datn_qlnt_manager.common.AssetStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssetFilter {
    String nameAsset;
    AssetBeLongTo assetBeLongTo;
    AssetStatus assetStatus;
}
