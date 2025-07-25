package com.example.datn_qlnt_manager.dto.response.servicePriceHistory;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
