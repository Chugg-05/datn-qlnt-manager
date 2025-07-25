package com.example.datn_qlnt_manager.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

import com.example.datn_qlnt_manager.common.MeterType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "cong_to",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"ma_cong_to", "phong_id"})})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Meter extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_id", nullable = false)
    Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dich_vu_id")
    Service service;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_cong_to", nullable = false)
    MeterType meterType;

    @Column(name = "ten_cong_to", nullable = false)
    String meterName;

    @Column(name = "ma_cong_to", nullable = false)
    String meterCode;

    @Column(name = "ngay_san_xuat")
    LocalDate manufactureDate;

    @Column(name = "so_gan_nhat", nullable = false)
    Integer closestIndex;

    @Column(name = "mo_ta")
    String descriptionMeter;
}
