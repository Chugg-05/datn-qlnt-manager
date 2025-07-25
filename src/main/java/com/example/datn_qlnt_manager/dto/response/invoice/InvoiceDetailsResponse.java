package com.example.datn_qlnt_manager.dto.response.invoice;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import com.example.datn_qlnt_manager.common.InvoiceStatus;
import com.example.datn_qlnt_manager.common.InvoiceType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceDetailsResponse {
    String invoiceId;
    String invoiceCode;
    String buildingName;
    String roomCode;
    String tenantName;
    String tenantPhone;
    Integer month;
    Integer year;
    LocalDate paymentDueDate;
    InvoiceStatus invoiceStatus;
    InvoiceType invoiceType;
    List<InvoiceItemResponse> items;
    BigDecimal totalAmount;
    String note;
    Instant createdAt;
    Instant updatedAt;
}
