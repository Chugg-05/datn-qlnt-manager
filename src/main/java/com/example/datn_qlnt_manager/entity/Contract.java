package com.example.datn_qlnt_manager.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.*;

import com.example.datn_qlnt_manager.common.ContractStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "hop_dong")
public class Contract extends AbstractEntity {

    @Column(name = "ma_hop_dong", unique = true, nullable = false)
    String contractCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_id", nullable = false)
    Room room;

    @Column(name = "so_luong_nguoi", nullable = false)
    Integer numberOfPeople;

    @Column(name = "ngay_bat_dau", nullable = false)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    LocalDateTime startDate;

    @Column(name = "ngay_ket_thuc", nullable = false)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    LocalDateTime endDate;

    @Column(name = "tien_coc", nullable = false)
    BigDecimal deposit;

    @Column(name = "tien_phong", nullable = false)
    BigDecimal roomPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    ContractStatus status;

    @Column(name = "gia_dien")
    BigDecimal electricPrice;

    @Column(name = "gia_nuoc")
    BigDecimal waterPrice;

    @Column(name = "noi_dung", nullable = false)
    String content;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "hop_dong_khach_thue",
            joinColumns = @JoinColumn(name = "hop_dong_id"),
            inverseJoinColumns = @JoinColumn(name = "khach_thue_id"))
    Set<Tenant> tenants;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "hop_dong_tai_san",
            joinColumns = @JoinColumn(name = "hop_dong_id"),
            inverseJoinColumns = @JoinColumn(name = "tai_san_id"))
    Set<Asset> assets;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "hop_dong_dich_vu",
            joinColumns = @JoinColumn(name = "hop_dong_id"),
            inverseJoinColumns = @JoinColumn(name = "dich_vu_id"))
    Set<Service> services;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "hop_dong_phuong_tien",
            joinColumns = @JoinColumn(name = "hop_dong_id"),
            inverseJoinColumns = @JoinColumn(name = "phuong_tien_id"))
    Set<Vehicle> vehicles;
}
