package com.example.datn_qlnt_manager.entity;

import com.example.datn_qlnt_manager.common.MeterType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cong_to_dien_nuoc")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Meter extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_id", nullable = false)
    Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dich_vu_id")
    Service service;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_cong_to", nullable = false, length = 10)
    MeterType meterType;

    @Column(name = "ten_cong_to", nullable = false, length = 100)
    String meterName;

    @Column(name = "ma_cong_to", nullable = false, length = 50, unique = true)
    String meterCode;

    @Column(name = "ngay_san_xuat")
    LocalDate manufactureDate;

    @Column(name = "chi_so_dau", nullable = false)
    Integer initialIndex;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    String description;

}
