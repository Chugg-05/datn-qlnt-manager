package com.example.datn_qlnt_manager.entity;

import jakarta.persistence.*;

import com.example.datn_qlnt_manager.common.FloorStatus;
import com.example.datn_qlnt_manager.common.FloorType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "tang",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"toa_nha_id", "ten_tang"})})
public class Floor extends AbstractEntity {

    @Column(name = "ten_tang", nullable = false)
    String nameFloor;

    @Column(name = "so_phong_toi_da", nullable = false)
    Integer maximumRoom;

    @Column(name = "loai_tang", nullable = false)
    @Enumerated(EnumType.STRING)
    FloorType floorType;

    @Column(name = "trang_thai", nullable = false)
    @Enumerated(EnumType.STRING)
    FloorStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_truoc_do", nullable = false)
    FloorStatus previousStatus;

    @Column(name = "mo_ta")
    String descriptionFloor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toa_nha_id", nullable = false)
    Building building;
}
