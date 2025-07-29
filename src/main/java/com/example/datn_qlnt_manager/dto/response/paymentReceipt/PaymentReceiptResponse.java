package com.example.datn_qlnt_manager.dto.response.paymentReceipt;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

import com.example.datn_qlnt_manager.common.PaymentMethod;
import com.example.datn_qlnt_manager.common.PaymentStatus;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentReceiptResponse {
    String id;
    String invoiceId;
    String invoiceCode;
    String receiptCode;
    BigDecimal amount;
    PaymentMethod paymentMethod;
    PaymentStatus paymentStatus;
    String collectedBy;
    LocalDateTime paymentDate;
    String note;
    Instant createdAt;
    Instant updatedAt;
}
