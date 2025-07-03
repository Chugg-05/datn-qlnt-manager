package com.example.datn_qlnt_manager.dto.response.invoice;

import com.example.datn_qlnt_manager.common.InvoiceStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceDetailsResponse {
    String invoiceCode;
    String buildingName;
    String roomCode;
    String tenantName;
    String tenantPhoneNumber;
    Integer month;
    Integer year;
    Integer oldNumber;
    Integer newNumber;
    Integer quantity;
    BigDecimal unitPrice;
    BigDecimal totalAmount;
    BigDecimal grantTotal;
    LocalDate paymentDueDate;
    InvoiceStatus invoiceStatus;
    String note;
    Instant createdAt;
    Instant updatedAt;
}
