package com.example.datn_qlnt_manager.entity;

import com.example.datn_qlnt_manager.common.DepositStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "tien_coc")
public class Deposit extends AbstractEntity {
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "hop_dong_id", nullable = false, unique = true)
    Contract contract;

    @Column(name = "nguoi_coc", nullable = false)
    String depositor;

    @Column(name = "nguoi_nhan", nullable = false)
    String depositRecipient;

    @Column(name = "so_tien_coc", nullable = false)
    BigDecimal depositAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    DepositStatus depositStatus;

    @Column(name = "ngay_dat_coc", nullable = false)
    LocalDateTime depositDate;

    @Column(name = "ngay_hoan_coc")
    LocalDateTime depositRefundDate;

    @Column(name = "ngay_nhan_lai_coc")
    LocalDateTime securityDepositReturnDate;

    @Column(name = "ghi_chu")
    String note;
}
