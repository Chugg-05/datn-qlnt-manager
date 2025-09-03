package com.example.datn_qlnt_manager.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import com.example.datn_qlnt_manager.common.BuildingStatus;
import jakarta.persistence.*;

import com.example.datn_qlnt_manager.common.ContractStatus;

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

    @Column(name = "ma_hop_dong", nullable = false)
    String contractCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_id", nullable = false)
    Room room;

    @Column(name = "ma_phong", nullable = false)
    String roomCode;

    @Column(name = "ngay_bat_dau", nullable = false)
    LocalDate startDate;

    @Column(name = "ngay_ket_thuc", nullable = false)
    LocalDate endDate;

    @Column(name = "tien_coc", nullable = false)
    BigDecimal deposit;

    @Column(name = "tien_phong", nullable = false)
    BigDecimal roomPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    ContractStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_truoc_do")
    ContractStatus previousContractStatus;

    @Column(name = "gia_dien")
    BigDecimal electricPrice;

    @Column(name = "gia_nuoc")
    BigDecimal waterPrice;

    @Column(name = "noi_dung")
    String content;

    @Column(name = "ngay_ket_thuc_ban_dau")
    LocalDate originalEndDate;

    @OneToMany(mappedBy = "contract", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    Set<ContractTenant> contractTenants;

    @OneToMany(mappedBy = "contract", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    Set<ContractVehicle> contractVehicles;

    @Column(name = "ngay_xoa")
    LocalDate deletedAt;
}
