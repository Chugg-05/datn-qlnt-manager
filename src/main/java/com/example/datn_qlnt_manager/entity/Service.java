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
//    @Id
//    @Column(length = 36)
//    private String id;

    @Column(name = "ten_dich_vu", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_dich_vu", nullable = false)
    private ServiceType type;

    @Column(name = "don_vi_tinh", nullable = false, length = 50)
    private String unit;

    @Column(name = "don_gia", nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "ap_dung_theo")
    private ServiceAppliedBy appliedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private ServiceStatus status;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String description;


}
