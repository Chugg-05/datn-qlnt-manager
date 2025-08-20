package com.example.datn_qlnt_manager.dto.request.serviceRoom;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceRoomCreationForServiceRequest {

    @NotBlank(message = "SERVICE_IDS_REQUIRED")
    String serviceId;

    @NotEmpty(message = "ROOM_ID_REQUIRED")
    List<String> roomIds;
}
