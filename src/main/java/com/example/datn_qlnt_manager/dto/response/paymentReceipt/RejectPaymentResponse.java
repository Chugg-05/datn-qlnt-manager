package com.example.datn_qlnt_manager.dto.response.paymentReceipt;

import com.example.datn_qlnt_manager.common.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RejectPaymentResponse {
    String id;
    PaymentStatus paymentStatus;
}
