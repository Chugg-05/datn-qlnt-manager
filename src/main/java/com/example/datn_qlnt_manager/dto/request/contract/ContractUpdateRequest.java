package com.example.datn_qlnt_manager.dto.request.contract;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.example.datn_qlnt_manager.common.ContractStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

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

    @NotNull(message = "INVALID_ASSETS_BLANK")
    @Size(min = 1, message = "INVALID_ASSETS")
    Set<String> assets;

    Set<String> services;

    Set<String> vehicles;

    @NotBlank(message = "INVALID_CONTENT_IN_CONTRACT")
    String content;
}
