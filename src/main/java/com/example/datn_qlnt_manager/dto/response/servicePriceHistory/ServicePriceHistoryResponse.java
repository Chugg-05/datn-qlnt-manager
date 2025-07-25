package com.example.datn_qlnt_manager.dto.response.servicePriceHistory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServicePriceHistoryResponse {
    String id;
    String serviceName;
    BigDecimal oldPrice;
    BigDecimal newPrice;
    LocalDateTime applicableDate;
}
