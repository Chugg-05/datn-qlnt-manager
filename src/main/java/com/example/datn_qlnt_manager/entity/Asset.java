package com.example.datn_qlnt_manager.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import com.example.datn_qlnt_manager.common.AssetBeLongTo;
import com.example.datn_qlnt_manager.common.AssetStatus;
import com.example.datn_qlnt_manager.common.AssetType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tai_san")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Asset extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toa_nha_id", nullable = false)
    Building building;

    @Column(name = "ten_tai_san", nullable = false)
    String nameAsset;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_tai_san", nullable = false)
    AssetType assetType;

    @Enumerated(EnumType.STRING)
    @Column(name = "thuoc_ve", nullable = false)
    AssetBeLongTo assetBeLongTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tinh_trang", nullable = false)
    AssetStatus assetStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "tinh_trang_truoc_do")
    AssetStatus previousStatus;

    @Column(name = "gia_tien", nullable = false)
    BigDecimal price;

    @Column(name = "so_luong", nullable = false)
    Integer quantity;

    @Column(name = "con_lai", nullable = false)
    Integer remainingQuantity;

    @Column(name = "mo_ta")
    String descriptionAsset;

    @Builder.Default
    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL, orphanRemoval = true) // đã có assetRooms
    Set<AssetRoom> assetRooms = new HashSet<>();

    @Column(name = "ngay_xoa")
    LocalDate deletedAt;
}
