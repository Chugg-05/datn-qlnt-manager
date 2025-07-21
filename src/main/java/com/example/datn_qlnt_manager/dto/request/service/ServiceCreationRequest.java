package com.example.datn_qlnt_manager.dto.request.service;

import com.example.datn_qlnt_manager.common.ServiceCalculation;
import com.example.datn_qlnt_manager.common.ServiceCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceCreationRequest {

    @NotBlank(message = "INVALID_SERVICE_NAME_BLANK")
    String name;

    @NotNull(message = "INVALID_SERVICE_CATEGORY_NULL")
    ServiceCategory serviceCategory;

    String unit;

    @NotNull(message = "INVALID_PRICE_NULL")
    @Min(value = 0, message = "INVALID_PRICE")
    BigDecimal price;

    @NotNull(message = "INVALID_SERVICE_CALCULATION_NULL")
    ServiceCalculation serviceCalculation;

    String description;

}
