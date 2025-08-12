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
public class ServiceRoomView {
    String id;
    String roomCode;
    int totalServices;
    RoomType roomType;
    RoomStatus status;
    String description;
}
