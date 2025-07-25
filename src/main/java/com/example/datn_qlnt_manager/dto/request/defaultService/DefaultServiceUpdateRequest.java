package com.example.datn_qlnt_manager.dto.request.defaultService;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import com.example.datn_qlnt_manager.common.DefaultServiceAppliesTo;
import com.example.datn_qlnt_manager.common.DefaultServiceStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DefaultServiceUpdateRequest {
    @NotNull(message = "INVALID_DEFAULT_SERVICE_APPLIES_TO_NULL")
    DefaultServiceAppliesTo defaultServiceAppliesTo;

    @Min(value = 0, message = "PRICES_APPLY_INVALID")
    BigDecimal pricesApply;

    @NotNull(message = "INVALID_DEFAULT_SERVICE_STATUS_NULL")
    DefaultServiceStatus defaultServiceStatus;

    String description;
}
