package com.example.datn_qlnt_manager.dto.request.contract;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractCreationRequest {
    @NotBlank(message = "ROOM_NOT_FOUND")
    String roomId;

    @Min(value = 1, message = "INVALID_NUMBER_OF_PEOPLE")
    Integer numberOfPeople;

    @NotNull(message = "INVALID_START_DATE_BLANK")
    LocalDateTime startDate;

    @NotNull(message = "INVALID_END_DATE_BLANK")
    LocalDateTime endDate;

    @NotNull(message = "INVALID_DEPOSIT_BLANK")
    @DecimalMin(value = "0.0", inclusive = false, message = "INVALID_DEPOSIT")
    BigDecimal deposit;

    @NotNull(message = "INVALID_TENANTS_BLANK")
    @Size(min = 1, message = "INVALID_TENANTS")
    Set<String> tenants;
}