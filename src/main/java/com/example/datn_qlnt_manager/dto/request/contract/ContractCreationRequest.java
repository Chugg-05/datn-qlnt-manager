package com.example.datn_qlnt_manager.dto.request.contract;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import com.example.datn_qlnt_manager.dto.request.ContractTenant.ContractTenantCreationRequest;
import jakarta.validation.constraints.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractCreationRequest {
    @NotBlank(message = "ROOM_NOT_FOUND")
    String roomId;

    @NotNull(message = "INVALID_START_DATE_BLANK")
    LocalDate startDate;

    @NotNull(message = "INVALID_END_DATE_BLANK")
    LocalDate endDate;

    @NotNull(message = "INVALID_DEPOSIT_BLANK")
    @DecimalMin(value = "0.0", inclusive = false, message = "INVALID_DEPOSIT")
    BigDecimal deposit;

    @NotNull(message = "INVALID_TENANTS_BLANK")
    @Size(min = 1, message = "INVALID_TENANTS")
    Set<ContractTenantCreationRequest> tenants;

    Set<String> vehicles;

    @NotBlank(message = "INVALID_CONTENT_IN_CONTRACT")
    String content;
}
