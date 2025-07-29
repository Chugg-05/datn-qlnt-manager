package com.example.datn_qlnt_manager.dto.request.paymentReceipt;


import com.example.datn_qlnt_manager.common.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentReceiptCreationRequest {
    @NotBlank(message = "INVOICE_ID_REQUIRED")
    String invoiceId;

    @NotNull(message = "PAYMENT_METHOD_REQUIRED")
    PaymentMethod paymentMethod;

    String note;
}
