package com.example.datn_qlnt_manager.dto.filter;

import com.example.datn_qlnt_manager.common.InvoiceStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

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
    BigDecimal minGrantTotal;
    BigDecimal maxGrantTotal;
    InvoiceStatus invoiceStatus;
}
