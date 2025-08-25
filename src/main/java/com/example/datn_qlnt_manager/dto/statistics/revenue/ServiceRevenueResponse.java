package com.example.datn_qlnt_manager.dto.statistics.revenue;

import com.example.datn_qlnt_manager.common.ServiceCategory;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceRevenueResponse {
    String serviceName;
    ServiceCategory category;
    BigDecimal amount;
}
