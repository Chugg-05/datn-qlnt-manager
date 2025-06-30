package com.example.datn_qlnt_manager.dto.response.defaultService;

import com.example.datn_qlnt_manager.common.DefaultServiceAppliesTo;
import com.example.datn_qlnt_manager.common.DefaultServiceStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DefaultServiceResponse {
    String id;
    DefaultServiceAppliesTo defaultServiceAppliesTo;
    BigDecimal pricesApply;
    LocalDate startApplying;
    DefaultServiceStatus defaultServiceStatus;
    String buildingName;
    String floorName;
    String serviceName;
    String description;
    Instant createdAt;
    Instant updatedAt;
}
