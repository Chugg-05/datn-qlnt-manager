package com.example.datn_qlnt_manager.dto.request.serviceRoom;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceRoomCreationRequest {
    @NotBlank(message = "ROOM_ID_REQUIRED")
    String roomId;

    @NotBlank(message = "SERVICE_ID_REQUIRED")
    String serviceId;

    @NotNull(message = "START_DATE_REQUIRED")
    @FutureOrPresent(message = "START_DATE_MUST_BE_TODAY_OR_FUTURE")
    LocalDate startDate;

    @NotNull(message = "TOTAL_PRICE_REQUIRED")
    @DecimalMin(value = "0.0", message = "TOTAL_PRICE_MUST_BE_NON_NEGATIVE")
    BigDecimal totalPrice;

    String descriptionServiceRoom;
}
