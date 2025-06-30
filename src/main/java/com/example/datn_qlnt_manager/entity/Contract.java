package com.example.datn_qlnt_manager.entity;

import com.example.datn_qlnt_manager.common.ContractStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "hop_dong")
public class Contract extends AbstractEntity {

    @Column(name = "ma_hop_dong", unique = true, nullable = false)
    String contractCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_id", nullable = false)
    Room room;

    @Column(name = "so_luong_nguoi", nullable = false)
    Integer numberOfPeople;

    @Column(name = "ngay_bat_dau", nullable = false)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    LocalDateTime startDate;

    @Column(name = "ngay_ket_thuc", nullable = false)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    LocalDateTime endDate;

    @Column(name = "tien_coc", nullable = false)
    BigDecimal deposit;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    ContractStatus status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "hop_dong_khach_thue",
            joinColumns = @JoinColumn(name = "hop_dong_id"),
            inverseJoinColumns = @JoinColumn(name = "khach_thue_id")
    )
    Set<Tenant> tenants;
}
