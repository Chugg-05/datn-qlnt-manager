package com.example.datn_qlnt_manager.dto.response.contract;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.example.datn_qlnt_manager.common.ContractStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractResponse {
    String id;
    String contractCode;
    String roomCode;
    LocalDate startDate;
    LocalDate endDate;
    BigDecimal deposit;
    BigDecimal roomPrice;
    ContractStatus status;
    Instant createdAt;
}
