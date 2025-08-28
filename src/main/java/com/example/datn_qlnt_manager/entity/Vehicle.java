package com.example.datn_qlnt_manager.entity;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

import jakarta.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.datn_qlnt_manager.common.VehicleStatus;
import com.example.datn_qlnt_manager.common.VehicleType;

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
@Table(name = "phuong_tien")
public class Vehicle extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_thue_id")
    Tenant tenant;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_phuong_tien")
    VehicleType vehicleType;

    @Column(name = "bien_so")
    String licensePlate;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    VehicleStatus vehicleStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_truoc_do")
    VehicleStatus previousVehicleStatus;

    @Column(name = "ngay_dang_ky")
    Date registrationDate;

    @Column(name = "mo_ta")
    String describe;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<ContractVehicle> contractVehicles;

    @Column(name = "ngay_xoa")
    LocalDate deleteAt;
}
