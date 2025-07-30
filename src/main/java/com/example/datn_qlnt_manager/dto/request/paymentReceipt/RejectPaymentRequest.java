package com.example.datn_qlnt_manager.dto.request.paymentReceipt;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RejectPaymentRequest {
    @NotBlank(message = "Lý do từ chối không được để trống")
    String reason;
}
