package com.example.datn_qlnt_manager.dto.request.contract;

import com.example.datn_qlnt_manager.common.ContractStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractUpdateRequest {
    @Min(value = 1, message = "INVALID_NUMBER_OF_PEOPLE")
    Integer numberOfPeople;

    @NotNull(message = "INVALID_END_DATE_BLANK")
    LocalDateTime endDate;

    @NotNull(message = "INVALID_CONTRACT_STATUS_BLANK")
    ContractStatus status;

    @NotNull(message = "INVALID_TENANTS_BLANK")
    @Size(min = 1, message = "INVALID_TENANTS")
    Set<String> tenants;
}