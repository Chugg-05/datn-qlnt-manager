package com.example.datn_qlnt_manager.entity;

import com.example.datn_qlnt_manager.common.AssetGroup;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "loai_tai_san",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ten_loai", "nhom_loai"})
})
public class AssetType extends AbstractEntity{
    @Column(name = "ten_loai", nullable = false)
    String nameAssetType;

    @Enumerated(EnumType.STRING)
    @Column(name = "nhom_loai",nullable = false)
    AssetGroup assetGroup;

    @Column(name = "mo_ta")
    String discriptionAssetType;
}
