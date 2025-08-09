package com.example.datn_qlnt_manager.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.datn_qlnt_manager.common.InvoiceItemType;

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
@Table(name = "chi_tiet_hoa_don")
public class InvoiceDetail extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hoa_don_id", nullable = false)
    Invoice invoice;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_chi_tiet", nullable = false)
    InvoiceItemType invoiceItemType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dich_vu_phong_id")
    ServiceRoom serviceRoom;

    @Column(name = "ten_dich_vu")
    String serviceName;

    @Column(name = "so_cu")
    Integer oldIndex;

    @Column(name = "so_moi")
    Integer newIndex;

    @Column(name = "so_luong", nullable = false)
    Integer quantity;

    @Column(name = "don_gia", nullable = false)
    BigDecimal unitPrice;

    @Column(name = "thanh_tien")
    BigDecimal amount;

    @Column(name = "mo_ta")
    String description;

    public void recalculateAmount() {
        if (unitPrice != null && quantity != null) {
            this.amount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public void recalculateQuantityFromIndex() {
        if (oldIndex != null && newIndex != null) {
            this.quantity = newIndex - oldIndex;
        }
    }
}
