package com.example.datn_qlnt_manager.dto.filter;

import java.math.BigDecimal;

import com.example.datn_qlnt_manager.common.InvoiceStatus;
import com.example.datn_qlnt_manager.common.InvoiceType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceFilter {
    String query;
    String building;
    String floor;
    Integer month;
    Integer year;
    BigDecimal minTotalAmount;
    BigDecimal maxTotalAmount;
    InvoiceStatus invoiceStatus;
    InvoiceType invoiceType;
}
