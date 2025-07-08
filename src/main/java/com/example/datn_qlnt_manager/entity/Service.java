package com.example.datn_qlnt_manager.entity;


import com.example.datn_qlnt_manager.common.ServiceAppliedBy;
import com.example.datn_qlnt_manager.common.ServiceStatus;
import com.example.datn_qlnt_manager.common.ServiceType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_dich_vu", nullable = false)
    ServiceType type;

    @Column(name = "don_vi_tinh", nullable = false, length = 50)
    String unit;

    @Column(name = "don_gia", nullable = false, precision = 15, scale = 2)
    BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "ap_dung_theo")
    ServiceAppliedBy appliedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    ServiceStatus status;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

}
