package com.example.datn_qlnt_manager.dto.response.service;

import java.math.BigDecimal;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceUpdateUnitPriceResponse {
    int totalUpdated;
    BigDecimal newUnitPrice;
    String serviceName;
    String buildingName;
}
