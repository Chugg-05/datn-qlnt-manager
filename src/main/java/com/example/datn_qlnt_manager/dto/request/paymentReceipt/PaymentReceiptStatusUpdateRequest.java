package com.example.datn_qlnt_manager.dto.request.paymentReceipt;

import jakarta.validation.constraints.NotNull;

import com.example.datn_qlnt_manager.common.PaymentStatus;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentReceiptStatusUpdateRequest {
    @NotNull(message = "PAYMENT_STATUS_REQUIRED")
    PaymentStatus paymentStatus;
}
