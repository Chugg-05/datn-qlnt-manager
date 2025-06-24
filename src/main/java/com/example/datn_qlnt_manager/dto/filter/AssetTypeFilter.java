package com.example.datn_qlnt_manager.dto.filter;
import com.example.datn_qlnt_manager.common.AssetGroup;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssetTypeFilter {
    String nameAssetType;
    AssetGroup assetGroup;
}
