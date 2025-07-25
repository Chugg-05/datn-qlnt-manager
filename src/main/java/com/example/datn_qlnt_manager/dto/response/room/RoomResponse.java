package com.example.datn_qlnt_manager.dto.response.room;

import java.time.Instant;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.common.RoomType;
import com.example.datn_qlnt_manager.dto.response.floor.FloorResponse;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomResponse {

    String id;
    String roomCode;
    Double acreage;
    Double price;
    Long maximumPeople;
    RoomType roomType;
    RoomStatus status;
    String description;
    FloorResponse floor;
    Instant createdAt;
    Instant updatedAt;
}
