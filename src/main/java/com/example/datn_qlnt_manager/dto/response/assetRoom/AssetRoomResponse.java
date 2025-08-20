package com.example.datn_qlnt_manager.dto.response.assetRoom;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.example.datn_qlnt_manager.common.AssetBeLongTo;
import com.example.datn_qlnt_manager.common.AssetStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssetRoomResponse {
    String id;

    String roomCode;

    AssetBeLongTo assetBeLongTo;

    String assetName;

    Integer quantity;

    BigDecimal price;

    LocalDate dateAdded;

    LocalDate takeAwayDay;

    AssetStatus assetStatus;

    String description;

    Instant createdAt;

    Instant updatedAt;
}
