package com.example.datn_qlnt_manager.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.datn_qlnt_manager.common.InvoiceStatus;
import com.example.datn_qlnt_manager.common.InvoiceType;

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
@Table(name = "hoa_don")
public class Invoice extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hop_dong_id", nullable = false)
    Contract contract;

    @Column(name = "ma_hoa_don", nullable = false)
    String invoiceCode;

    @Column(name = "tong_tien", nullable = false)
    BigDecimal totalAmount;

    @Column(name = "thang", nullable = false)
    Integer month;

    @Column(name = "nam", nullable = false)
    Integer year;

    @Column(name = "han_thanh_toan", nullable = false)
    LocalDate paymentDueDate;

    @Column(name = "trang_thai", nullable = false)
    @Enumerated(EnumType.STRING)
    InvoiceStatus invoiceStatus;

    @Column(name = "loai_hoa_don", nullable = false)
    @Enumerated(EnumType.STRING)
    InvoiceType invoiceType;

    @Column(name = "ghi_chu")
    String note;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceDetail> details = new ArrayList<>();
}
