package com.example.datn_qlnt_manager.dto.response.invoice;

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
public class InvoiceResponse {
    String id;
    String invoiceCode;
    String buildingName;
    String roomCode;
    String tenantName;
    Integer month;
    Integer year;
    BigDecimal totalAmount;
    LocalDate paymentDueDate;
    InvoiceStatus invoiceStatus;
    InvoiceType invoiceType;
    String note;
    Instant createdAt;
}
