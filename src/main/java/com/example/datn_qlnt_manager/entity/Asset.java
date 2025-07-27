package com.example.datn_qlnt_manager.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.example.datn_qlnt_manager.common.AssetType;
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

    @ManyToOne(fetch = FetchType.LAZY)
    User user;

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

    @Column(name = "gia_tien", nullable = false)
    BigDecimal price;

    @Column(name = "mo_ta")
    String descriptionAsset;

    @ManyToMany(mappedBy = "assets")
    Set<Room> rooms = new HashSet<>();
}
