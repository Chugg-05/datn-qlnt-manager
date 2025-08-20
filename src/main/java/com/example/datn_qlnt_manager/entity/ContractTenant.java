package com.example.datn_qlnt_manager.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "hop_dong_khach_thue")
public class ContractTenant extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hop_dong_id", nullable = false)
    Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_thue_id", nullable = false)
    Tenant tenant;

    @Column(name = "dai_dien", nullable = false)
    boolean representative = false;

    @Column(name = "ngay_bat_dau", nullable = false)
    LocalDate startDate;

    @Column(name = "ngay_ket_thuc")
    LocalDate endDate;

}
