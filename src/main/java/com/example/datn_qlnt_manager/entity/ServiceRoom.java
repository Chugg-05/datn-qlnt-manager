package com.example.datn_qlnt_manager.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.example.datn_qlnt_manager.common.ServiceRoomStatus;

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
        name = "dich_vu_phong",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"phong_id", "dich_vu_id"})})
public class ServiceRoom extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_id", nullable = false)
    Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dich_vu_id", nullable = false)
    Service service;

    @Column(name = "don_gia_ap_dung", nullable = false)
    BigDecimal unitPrice;

    @Column(name = "ngay_bat_dau", nullable = false)
    LocalDate startDate;

    @Column(name = "ngay_ket_thuc")
    LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    ServiceRoomStatus serviceRoomStatus;

    @Column(name = "mo_ta")
    String description;
}
