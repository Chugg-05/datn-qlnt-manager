package com.example.datn_qlnt_manager.dto.projection;

import com.example.datn_qlnt_manager.common.DepositStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DepositDetailView {
        String id;
        String contractId;
        String contractCode;
        String roomCode;
        String depositor;
        String depositRecipient;
        BigDecimal depositAmount;
        DepositStatus depositStatus;
        BigDecimal refundAmount;
        LocalDateTime depositDate;
        LocalDateTime depositRefundDate;
        LocalDateTime securityDepositReturnDate;
        String note;
        Instant createdAt;
        Instant updatedAt;
}
