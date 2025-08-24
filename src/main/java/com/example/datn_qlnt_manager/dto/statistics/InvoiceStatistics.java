package com.example.datn_qlnt_manager.dto.statistics;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceStatistics {
    Long total;
    Long totalNotYetPaid;
    Long totalWaitingForPayment;
    Long totalPaid;
    Long totalCannotBePaid;
    Long totalOverdue;
    Long totalCancelled;
}
