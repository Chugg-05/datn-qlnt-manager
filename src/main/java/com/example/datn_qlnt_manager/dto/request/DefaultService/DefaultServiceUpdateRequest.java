package com.example.datn_qlnt_manager.dto.request.DefaultService;

import com.example.datn_qlnt_manager.common.DefaultServiceAppliesTo;
import com.example.datn_qlnt_manager.common.DefaultServiceStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DefaultServiceUpdateRequest {
    DefaultServiceAppliesTo defaultServiceAppliesTo;
    BigDecimal pricesApply;
    DefaultServiceStatus defaultServiceStatus;
    String description;

}
