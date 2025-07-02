package com.example.datn_qlnt_manager.entity;

import jakarta.persistence.*;

import com.example.datn_qlnt_manager.common.AssetGroup;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
        name = "loai_tai_san",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"ten_loai", "nhom_loai", "user_id"})})
public class AssetType extends AbstractEntity {
    @Column(name = "ten_loai", nullable = false)
    String nameAssetType;

    @Enumerated(EnumType.STRING)
    @Column(name = "nhom_loai", nullable = false)
    AssetGroup assetGroup;

    @Column(name = "mo_ta")
    String discriptionAssetType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
