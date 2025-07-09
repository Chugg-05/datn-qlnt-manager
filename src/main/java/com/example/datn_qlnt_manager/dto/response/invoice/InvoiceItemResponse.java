package com.example.datn_qlnt_manager.dto.response.invoice;

import com.example.datn_qlnt_manager.common.ServiceType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceItemResponse {
    String serviceName;
    ServiceType serviceType;
    Integer oldIndex;        // optional
    Integer newIndex;        // optional
    Integer quantity;
    BigDecimal unitPrice;
    BigDecimal amount;

}
