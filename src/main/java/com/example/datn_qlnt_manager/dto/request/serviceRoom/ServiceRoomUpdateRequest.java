package com.example.datn_qlnt_manager.dto.request.serviceRoom;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.example.datn_qlnt_manager.common.ServiceRoomStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceRoomUpdateRequest {
    @NotBlank(message = "ROOM_ID_REQUIRED")
    String roomId;

    @NotBlank(message = "SERVICE_ID_REQUIRED")
    String serviceId;

    @NotNull(message = "START_DATE_REQUIRED")
    LocalDate startDate;

    @NotNull(message = "TOTAL_PRICE_REQUIRED")
    @DecimalMin(value = "0.0", message = "TOTAL_PRICE_MUST_BE_NON_NEGATIVE")
    BigDecimal totalPrice;

    @NotNull(message = "STATUS_REQUIRED")
    ServiceRoomStatus serviceRoomStatus;

    String descriptionServiceRoom;
}
