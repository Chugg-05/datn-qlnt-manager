package com.example.datn_qlnt_manager.dto.request.contract;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractUpdateRequest {

    @NotNull(message = "INVALID_END_DATE_BLANK")
    LocalDate endDate;

    @NotNull(message = "INVALID_DEPOSIT_BLANK")
    @DecimalMin(value = "0.0", inclusive = false, message = "INVALID_DEPOSIT")
    BigDecimal deposit;
}
