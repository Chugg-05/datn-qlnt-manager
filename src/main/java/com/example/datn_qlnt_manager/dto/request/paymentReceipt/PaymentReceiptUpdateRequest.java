package com.example.datn_qlnt_manager.dto.request.paymentReceipt;

import jakarta.validation.constraints.NotNull;

import com.example.datn_qlnt_manager.common.PaymentMethod;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentReceiptUpdateRequest {
    @NotNull(message = "PAYMENT_METHOD_REQUIRED")
    PaymentMethod paymentMethod;

    String note;
}
