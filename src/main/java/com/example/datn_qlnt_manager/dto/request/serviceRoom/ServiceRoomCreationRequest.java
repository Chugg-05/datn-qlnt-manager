package com.example.datn_qlnt_manager.dto.request.serviceRoom;

import jakarta.validation.constraints.NotBlank;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceRoomCreationRequest {
    @NotBlank(message = "ROOM_ID_REQUIRED")
    String roomId;

    @NotBlank(message = "SERVICE_IDS_REQUIRED")
    String serviceId;
}
