package com.example.datn_qlnt_manager.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.common.RoomType;

import lombok.*;
import lombok.experimental.FieldDefaults;

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

    @Column(name = "ma_phong", nullable = false, length = 20)
    String roomCode;

    @Column(name = "dien_tich", nullable = false, precision = 3, scale = 2)
    BigDecimal acreage;

    @Column(name = "gia", nullable = false, precision = 15, scale = 2)
    BigDecimal price;

    @Builder.Default
    @Column(name = "so_nguoi_toi_da", nullable = false)
    Integer maximumPeople = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_phong")
    RoomType roomType;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    RoomStatus status;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tang_id")
    Floor floor;
}
