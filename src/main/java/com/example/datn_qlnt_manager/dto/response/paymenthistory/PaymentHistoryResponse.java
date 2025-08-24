package com.example.datn_qlnt_manager.dto.response.paymenthistory;

import com.example.datn_qlnt_manager.common.PaymentAction;
import com.example.datn_qlnt_manager.common.PaymentMethod;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentHistoryResponse {
    String id;
    String receiptId;
    String receiptCode;
    String invoiceId;
    String invoiceCode;
    String roomCode;
    BigDecimal amount;
    PaymentAction paymentAction;
    PaymentMethod paymentMethod;
    LocalDateTime time;
    String note;
}
