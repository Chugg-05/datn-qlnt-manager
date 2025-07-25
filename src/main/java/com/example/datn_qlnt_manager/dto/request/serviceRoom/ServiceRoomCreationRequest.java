package com.example.datn_qlnt_manager.dto.request.serviceRoom;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
    LocalDate startDate;

    String descriptionServiceRoom;
}
