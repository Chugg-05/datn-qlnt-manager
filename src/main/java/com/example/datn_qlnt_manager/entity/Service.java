package com.example.datn_qlnt_manager.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;

import com.example.datn_qlnt_manager.common.ServiceCalculation;
import com.example.datn_qlnt_manager.common.ServiceCategory;
import com.example.datn_qlnt_manager.common.ServiceStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "dich_vu")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Service extends AbstractEntity {

    @Column(name = "ten_dich_vu", nullable = false)
    String name;

    @Column(name = "don_vi_tinh", nullable = false, length = 50)
    String unit;

    @Column(name = "don_gia", nullable = false, precision = 15, scale = 2)
    BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "danh_muc", nullable = false)
    ServiceCategory serviceCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "cach_tinh")
    ServiceCalculation serviceCalculation;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    ServiceStatus status;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;
}
