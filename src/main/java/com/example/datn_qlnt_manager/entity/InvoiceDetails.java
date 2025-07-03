package com.example.datn_qlnt_manager.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "chi_tiet_hoa_don")
public class InvoiceDetails extends AbstractEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hoa_don_id", nullable = false)
    Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dich_vu_id", nullable = false)
    Service service;

    @Column(name = "so_luong", nullable = false)
    Integer quantity;

    @Column(name = "don_gia", nullable = false)
    BigDecimal unitPrice;

    @Column(name = "thanh_tien", insertable = false, updatable = false)
    BigDecimal totalAmount;

    @Column(name = "mo_ta")
    String description;
}
