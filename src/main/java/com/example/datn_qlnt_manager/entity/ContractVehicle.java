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
@Table(name = "hop_dong_phuong_tien",
        uniqueConstraints = @UniqueConstraint(columnNames = {"hop_dong_id", "phuong_tien_id"}))
    public class ContractVehicle extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hop_dong_id", nullable = false)
    Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phuong_tien_id", nullable = false)
    Vehicle vehicle;

    @Column(name = "ngay_bat_dau", nullable = false)
    LocalDate startDate;

    @Column(name = "ngay_ket_thuc")
    LocalDate endDate;

}

