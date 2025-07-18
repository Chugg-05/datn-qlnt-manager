package com.example.datn_qlnt_manager.dto.request.service;

import com.example.datn_qlnt_manager.common.ServiceCalculation;
import com.example.datn_qlnt_manager.common.ServiceStatus;
import com.example.datn_qlnt_manager.common.ServiceCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceCreationRequest {

    @NotBlank
    String name;

    @NotNull
    ServiceCategory type;

    @NotBlank
    String unit;

    @NotNull
    BigDecimal price;

    ServiceCalculation appliedBy;

    @NotNull
    ServiceStatus status;

    String description;

//    @NotNull
//    String userId;

}
