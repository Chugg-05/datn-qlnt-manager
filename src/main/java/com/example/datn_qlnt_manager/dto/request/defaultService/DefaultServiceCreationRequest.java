package com.example.datn_qlnt_manager.dto.request.defaultService;

import com.example.datn_qlnt_manager.common.DefaultServiceAppliesTo;
import com.example.datn_qlnt_manager.common.DefaultServiceStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DefaultServiceCreationRequest {
    @NotNull(message = "INVALID_DEFAULT_SERVICE_APPLIES_TO_NULL")
    DefaultServiceAppliesTo defaultServiceAppliesTo;

    @Min(value = 0, message = "PRICES_APPLY_INVALID")
    BigDecimal pricesApply;
    @NotNull(message = "INVALID_START_APPLYING_NULL")
    LocalDate startApplying;

    DefaultServiceStatus defaultServiceStatus;
    String description;
    @NotNull(message = "INVALID_BUILDING_ID_NULL")
    String buildingId;
    String floorId;
    @NotNull(message = "INVALID_SERVICE_ID_NULL")
    String serviceId;


}
