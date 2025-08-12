package com.example.datn_qlnt_manager.dto.response.room;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.common.RoomType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomBasicResponse {
    String id;
    String roomCode;
    RoomType roomType;
    RoomStatus status;
    String description;
}
