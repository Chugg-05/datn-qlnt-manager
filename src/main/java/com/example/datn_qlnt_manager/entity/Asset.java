package com.example.datn_qlnt_manager.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;

import com.example.datn_qlnt_manager.common.AssetBeLongTo;
import com.example.datn_qlnt_manager.common.AssetStatus;

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

    @Column(name = "ten_tai_san", nullable = false)
    String nameAsset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loai_tai_san_id", nullable = false)
    AssetType assetType;

    @Enumerated(EnumType.STRING)
    @Column(name = "thuoc_ve", nullable = false)
    AssetBeLongTo assetBeLongTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_id")
    Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toa_nha_id")
    Building building;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tang_id")
    Floor floor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_thue_id")
    Tenant tenant;

    @Column(name = "gia_tien", nullable = false)
    BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "tinh_trang", nullable = false)
    AssetStatus assetStatus;

    @Column(name = "mo_ta")
    String descriptionAsset;
}
