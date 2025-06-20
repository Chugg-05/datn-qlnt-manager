package com.example.datn_qlnt_manager.dto.response.room;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.common.RoomType;
import com.example.datn_qlnt_manager.entity.Floor;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomResponse {

    String id;
    String roomId;
    String roomName;
    Double acreage;
    Double price;
    Long maximumPeople;
    RoomType roomType;
    RoomStatus status;
    String description;
    // khi nao co FloorResponse thi dan no vao day
//    Floor floor;
}
