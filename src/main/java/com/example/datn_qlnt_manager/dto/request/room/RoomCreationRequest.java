package com.example.datn_qlnt_manager.dto.request.room;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.common.RoomType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomCreationRequest {
    // khi nao tao them thi truyen cai nay vao ko truyen ca entity
    String maPhong;
    Double dienTich;
    Double gia;
    Long soNguoiToiDa;
    RoomType loaiPhong;
    RoomStatus trangThai;
    String moTa;
}
