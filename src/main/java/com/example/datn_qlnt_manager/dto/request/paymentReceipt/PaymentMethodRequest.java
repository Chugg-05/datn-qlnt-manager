package com.example.datn_qlnt_manager.dto.request.paymentReceipt;

import com.example.datn_qlnt_manager.common.PaymentMethod;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentMethodRequest {
    PaymentMethod paymentMethod;

}
