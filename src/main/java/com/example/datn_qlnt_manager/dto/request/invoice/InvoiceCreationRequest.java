package com.example.datn_qlnt_manager.dto.request.invoice;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceCreationRequest {
    String contractId;
    Integer month;
    Integer year;
    LocalDate paymentDueDate;
    String note;
}
