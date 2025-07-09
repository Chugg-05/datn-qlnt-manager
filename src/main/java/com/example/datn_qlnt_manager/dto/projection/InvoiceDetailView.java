package com.example.datn_qlnt_manager.dto.projection;

import com.example.datn_qlnt_manager.common.InvoiceStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvoiceDetailView {
    String invoiceId;
    String invoiceCode;
    Integer month;
    Integer year;
    LocalDate paymentDueDate;
    InvoiceStatus invoiceStatus;
    BigDecimal totalAmount;
    String note;
    Instant createdAt;
    Instant updatedAt;

    String buildingName;
    String roomCode;

    String tenantName;
    String tenantPhone;
}
