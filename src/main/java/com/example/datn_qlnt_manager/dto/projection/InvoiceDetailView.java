package com.example.datn_qlnt_manager.dto.projection;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.example.datn_qlnt_manager.common.InvoiceStatus;
import com.example.datn_qlnt_manager.common.InvoiceType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceDetailView {
    String id;

    String invoiceCode;

    String ownerPhone;

    String buildingName;

    String address;

    String roomCode;

    String tenantName;

    String tenantPhone;

    Integer month;

    Integer year;

    LocalDate paymentDueDate;

    InvoiceStatus invoiceStatus;

    InvoiceType invoiceType;

    BigDecimal totalAmount;

    String note;

    Instant createdAt;

    Instant updatedAt;
}
