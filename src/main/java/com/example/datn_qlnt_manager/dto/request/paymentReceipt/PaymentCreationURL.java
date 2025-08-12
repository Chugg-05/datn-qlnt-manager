package com.example.datn_qlnt_manager.dto.request.paymentReceipt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentCreationURL {

    @NotNull
    Long amount;

    @Builder.Default
    String bankCode = "NCB";

    @NotBlank
    String transactionReferenceCode;

    @Builder.Default
    String language = "vn";
}
