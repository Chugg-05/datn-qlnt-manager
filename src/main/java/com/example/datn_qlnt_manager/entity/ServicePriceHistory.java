package com.example.datn_qlnt_manager.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "lich_su_gia_dich_vu")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServicePriceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    String id;

    @ManyToOne
    @JoinColumn(name = "dich_vu_id", nullable = false)
    Service service;

    @Column(name = "gia_cu")
    BigDecimal oldPrice;

    @Column(name = "gia_moi")
    BigDecimal newPrice;

    @Column(name = "ngay_ap_dung")
    LocalDateTime applicableDate;
}
