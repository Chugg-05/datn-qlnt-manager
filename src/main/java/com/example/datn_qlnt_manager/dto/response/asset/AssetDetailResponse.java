package com.example.datn_qlnt_manager.dto.response.asset;

import java.math.BigDecimal;
import java.util.List;

import com.example.datn_qlnt_manager.common.AssetStatus;
import com.example.datn_qlnt_manager.common.AssetType;
import com.example.datn_qlnt_manager.dto.response.room.RoomBasicResponse;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssetDetailResponse {
    String id;
    String nameAsset;
    AssetType assetType;
    AssetStatus assetStatus;
    BigDecimal price;
    String description;
    List<RoomBasicResponse> rooms;
}
