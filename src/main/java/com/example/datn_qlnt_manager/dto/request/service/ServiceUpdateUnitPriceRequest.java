package com.example.datn_qlnt_manager.dto.request.service;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceUpdateUnitPriceRequest {
    @NotBlank(message = "SERVICE_ID_REQUIRED")
    String serviceId;

    @NotBlank(message = "BUILDING_ID_REQUIRED")
    String buildingId;

    @NotNull(message = "NEW_UNIT_PRICE_REQUIRED")
    @DecimalMin(value = "0.0", inclusive = false, message = "UNIT_PRICE_MUST_BE_POSITIVE")
    BigDecimal newUnitPrice;
}
