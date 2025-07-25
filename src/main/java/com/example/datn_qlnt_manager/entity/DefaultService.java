package com.example.datn_qlnt_manager.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.datn_qlnt_manager.common.DefaultServiceAppliesTo;
import com.example.datn_qlnt_manager.common.DefaultServiceStatus;

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
@Table(name = "dich_vu_mac_dinh")
public class DefaultService extends AbstractEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "ap_dung_cho")
    DefaultServiceAppliesTo defaultServiceAppliesTo;

    @Column(name = "gia_ap_dung")
    BigDecimal pricesApply;

    @Column(name = "bat_dau_ap_dung")
    LocalDate startApplying;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    DefaultServiceStatus defaultServiceStatus;

    @Column(name = "mo_ta")
    String description;

    @ManyToOne
    @JoinColumn(name = "toa_nha_id")
    Building building;

    @ManyToOne
    @JoinColumn(name = "tang_id")
    Floor floor;

    @ManyToOne
    @JoinColumn(name = "dich_vu_id")
    Service service;
}
