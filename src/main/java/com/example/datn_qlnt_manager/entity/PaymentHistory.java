package com.example.datn_qlnt_manager.entity;

import com.example.datn_qlnt_manager.common.PaymentAction;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_thanh_toan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phieu_thanh_toan_id")
    PaymentReceipt paymentReceipt;

    @Enumerated(EnumType.STRING)
    @Column(name = "hanh_dong", nullable = false)
     PaymentAction paymentAction;

    @Column(name = "thoi_gian", nullable = false)
     LocalDateTime time;

    @Column(name = "ghi_chu")
     String note;
}
