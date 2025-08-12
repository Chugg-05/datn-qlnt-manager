package com.example.datn_qlnt_manager.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;

import com.example.datn_qlnt_manager.common.AssetBeLongTo;
import com.example.datn_qlnt_manager.common.AssetStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
        name = "tai_san_phong",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"phong_id", "tai_san_id"})})
public class AssetRoom extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_id", nullable = false)
    Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tai_san_id")
    Asset asset;

    @Enumerated(EnumType.STRING)
    @Column(name = "thuoc_ve", nullable = false)
    AssetBeLongTo assetBeLongTo;

    @Column(name = "ten_tai_san", nullable = false)
    String assetName;

    @Column(name = "gia_tien", nullable = false)
    BigDecimal price;

    @Column(name = "ngay_them_vao", nullable = false)
    LocalDate dateAdded;

    @Column(name = "ngay_mang_di")
    LocalDate takeAwayDay;

    @Enumerated(EnumType.STRING)
    @Column(name = "tinh_trang")
    AssetStatus assetStatus;

    @Column(name = "mo_ta")
    String description;
}
