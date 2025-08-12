package com.example.datn_qlnt_manager.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.example.datn_qlnt_manager.common.PaymentMethod;
import com.example.datn_qlnt_manager.common.PaymentStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
        name = "phieu_thanh_toan",
        uniqueConstraints = {@UniqueConstraint(columnNames = "so_phieu")})
public class PaymentReceipt extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hoa_don_id", nullable = false)
    Invoice invoice;

    @Column(name = "so_phieu", nullable = false)
    String receiptCode;

    @Column(name = "so_tien", nullable = false)
    BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "phuong_thuc", nullable = false)
    PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    PaymentStatus paymentStatus;

    @Column(name = "nguoi_thu", nullable = false)
    String collectedBy;

    @Column(name = "ngay_thanh_toan")
    LocalDateTime paymentDate;

    @Column(name = "ghi_chu")
    String note;
}
