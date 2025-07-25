package com.example.datn_qlnt_manager.dto.response.meter;

import java.math.BigDecimal;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.common.RoomType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomNoMeterResponse {
    String id;
    String nameFloor;
    String roomCode;
    BigDecimal price;
    RoomType roomType;
    RoomStatus status;
    String description;
}
