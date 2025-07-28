package com.example.datn_qlnt_manager.dto.filter;

import com.example.datn_qlnt_manager.common.PaymentMethod;
import com.example.datn_qlnt_manager.common.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentReceiptFilter {
    String query; // receiptCode, invoiceCode

    PaymentStatus paymentStatus;
    PaymentMethod paymentMethod;

    BigDecimal fromAmount;
    BigDecimal toAmount;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime toDate;
}
