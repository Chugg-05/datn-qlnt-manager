package com.example.datn_qlnt_manager.entity;

import jakarta.persistence.*;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.common.RoomType;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

// entity ko dung data
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "phong")
@FieldDefaults(level = AccessLevel.PRIVATE) // khong can ghi all la private
public class Room extends AbstractEntity {

    // sua ten theo tieng anh nha

    @Column(name = "ma_phong", nullable = false, unique = true, length = 20)
    String maPhong;

    @Column(name = "dien_tich", nullable = false, precision = 3, scale = 2)
    BigDecimal dienTich;

    @Column(name = "gia", nullable = false, precision = 15, scale = 2)
    BigDecimal gia;

    @Builder.Default
    @Column(name = "so_nguoi_toi_da", nullable = false)
    Integer soNguoiToiDa = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_phong")
    RoomType loaiPhong;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    RoomStatus trangThai;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    String moTa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tang_id")
    Floor floor;
}
