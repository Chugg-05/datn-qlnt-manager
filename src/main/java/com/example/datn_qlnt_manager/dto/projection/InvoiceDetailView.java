package com.example.datn_qlnt_manager.dto.projection;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.example.datn_qlnt_manager.common.InvoiceStatus;
import com.example.datn_qlnt_manager.common.InvoiceType;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDetailView {
    String invoiceId;
    String invoiceCode;
    Integer month;
    Integer year;
    LocalDate paymentDueDate;
    InvoiceStatus invoiceStatus;
    InvoiceType invoiceType;
    BigDecimal totalAmount;
    String note;
    Instant createdAt;
    Instant updatedAt;

    String buildingName;
    String roomCode;

    String tenantName;
    String tenantPhone;
}
