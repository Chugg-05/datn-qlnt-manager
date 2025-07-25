package com.example.datn_qlnt_manager.dto.filter;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

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
