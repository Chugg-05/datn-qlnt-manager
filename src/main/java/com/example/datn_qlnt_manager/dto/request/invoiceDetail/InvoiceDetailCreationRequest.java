package com.example.datn_qlnt_manager.dto.request.invoiceDetail;

import com.example.datn_qlnt_manager.common.InvoiceItemType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceDetailCreationRequest {
    @NotBlank(message = "INVOICE_NOT_FOUND")
    String invoiceId;

    @NotNull(message = "INVOICE_ITEM_TYPE_NOT_NULL")
    InvoiceItemType invoiceItemType;

    String serviceRoomId;

    @Min(value = 1 , message = "INVALID_NEW_INDEX_MIN")
    Integer newIndex;

    @Min(value = 1, message = "INVALID_QUANTITY_MIN")
    Integer quantity;

    String serviceName;

    @Min(value = 1, message = "INVALID_UNIT_PRICE_MIN")
    BigDecimal unitPrice;

    String description;
}
