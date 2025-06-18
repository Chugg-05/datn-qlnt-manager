package com.example.datn_qlnt_manager.entity;

import jakarta.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.datn_qlnt_manager.common.BuildingStatus;
import com.example.datn_qlnt_manager.common.BuildingType;

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
    @Column(name = "ma_toa_nha", unique = true)
    String buildingCode;

    @Column(name = "ten_toa_nha")
    String buildingName;

    @Column(name = "dia_chi")
    String address;

    @Column(name = "so_tang_thuc_te")
    Integer actualNumberOfFloors;

    @Column(name = "so_tang_cho_thue")
    Integer numberOfFloorsForRent;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_toa_nha")
    BuildingType buildingType;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    BuildingStatus status;

    @Column(name = "mo_ta")
    String description;

    @ManyToOne(fetch = FetchType.LAZY) // sửa
    @JoinColumn(name = "user_id")
    User user;
}
