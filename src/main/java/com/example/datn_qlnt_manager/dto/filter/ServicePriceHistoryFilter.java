package com.example.datn_qlnt_manager.dto.filter;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServicePriceHistoryFilter {
    String serviceName;
    BigDecimal minOldPrice;
    BigDecimal maxOldPrice;
    BigDecimal minNewPrice;
    BigDecimal maxNewPrice;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate endDate;
}
