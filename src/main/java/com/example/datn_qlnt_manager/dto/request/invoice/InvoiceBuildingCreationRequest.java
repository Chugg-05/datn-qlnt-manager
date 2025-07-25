package com.example.datn_qlnt_manager.dto.request.invoice;

import java.time.LocalDate;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceBuildingCreationRequest {
    String buildingId;
    Integer month;
    Integer year;
    LocalDate paymentDueDate;
    String note;
}
