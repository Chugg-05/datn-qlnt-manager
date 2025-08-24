package com.example.datn_qlnt_manager.dto.filter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import com.example.datn_qlnt_manager.common.PaymentMethod;
import com.example.datn_qlnt_manager.common.PaymentStatus;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentHistoryFilter {

     String query;

     PaymentStatus paymentStatus;
     PaymentMethod paymentMethod;

     BigDecimal fromAmount;
     BigDecimal toAmount;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
     LocalDateTime fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
     LocalDateTime toDate;
}
