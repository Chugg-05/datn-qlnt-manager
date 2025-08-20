package com.example.datn_qlnt_manager.dto.filter;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.common.RoomType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceRoomFilter {
    String query;
    String building;
    String floor;
    RoomType roomType;
    RoomStatus status;
}
