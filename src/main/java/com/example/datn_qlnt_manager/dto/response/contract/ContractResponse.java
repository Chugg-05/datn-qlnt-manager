package com.example.datn_qlnt_manager.dto.response.contract;

import com.example.datn_qlnt_manager.common.ContractStatus;
import com.example.datn_qlnt_manager.dto.response.tenant.TenantBasicResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractResponse {
    String id;
    String contractCode;
    String roomCode;
    Integer numberOfPeople;
    LocalDateTime startDate;
    LocalDateTime endDate;
    BigDecimal deposit;
    BigDecimal roomPrice;
    ContractStatus status;
    Instant createdAt;
    Set<TenantBasicResponse> tenants;
}

