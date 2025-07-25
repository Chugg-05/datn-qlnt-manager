package com.example.datn_qlnt_manager.dto.request.invoiceDetail;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceDetailUpdateRequest {
    @Min(value = 1, message = "INVALID_NEW_INDEX_MIN")
    Integer newIndex;

    @Min(value = 1, message = "INVALID_QUANTITY_MIN")
    Integer quantity;

    @Min(value = 1, message = "INVALID_UNIT_PRICE_MIN")
    BigDecimal unitPrice;

    String description;
}
