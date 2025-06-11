package com.example.datn_qlnt_manager.entity;

import jakarta.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.datn_qlnt_manager.common.BuildingStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "toa_nha")
public class Building extends AbstractEntity {
    @Column(name = "ma_toa_nha", nullable = false, unique = true)
    private String buildingCode;

    @Column(name = "ten_toa_nha", nullable = false)
    private String buildingName;

    @Column(name = "dia_chi", nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private BuildingStatus status;

    @Column(name = "mo_ta")
    private String description;
}
