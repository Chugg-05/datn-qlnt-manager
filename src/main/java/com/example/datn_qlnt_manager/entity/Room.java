package com.example.datn_qlnt_manager.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Room extends AbstractEntity {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_truoc_do",nullable = false)
    RoomStatus previousStatus;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tang_id")
    Floor floor;

    @Builder.Default
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<ServiceRoom> serviceRooms = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<AssetRoom> assetRooms = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Contract> contracts = new HashSet<>();

    @Column(name = "ngay_xoa")
    LocalDate deleteAt;
}
