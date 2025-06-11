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
    String maPhong;
    Double dienTich;
    Double gia;
    Long soNguoiToiDa;
    RoomType loaiPhong;
    RoomStatus trangThai;
    String moTa;
    // khi nao co FloorResponse thi dan no vao day
    Floor tang;
}
