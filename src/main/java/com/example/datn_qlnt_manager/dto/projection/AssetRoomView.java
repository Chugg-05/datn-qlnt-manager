package com.example.datn_qlnt_manager.dto.projection;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.common.RoomType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssetRoomView {
    String id;
    String roomCode;
    long totalAssets;
    RoomType roomType;
    RoomStatus status;
    String description;
}
