package com.example.datn_qlnt_manager.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

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

    @Column(name = "gia_dien")
    BigDecimal electricPrice;

    @Column(name = "gia_nuoc")
    BigDecimal waterPrice;

    @Column(name = "noi_dung")
    String content;

    @OneToMany(mappedBy = "contract", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    Set<ContractTenant> contractTenants;

    @OneToMany(mappedBy = "contract", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    Set<ContractVehicle> contractVehicles;
}
