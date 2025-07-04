package com.example.datn_qlnt_manager.dto.request.meter;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeterDeleteRequest {

    @NotBlank(message = "ID must not be blank")
    String id;

    @NotBlank(message = "Room code must not be blank")
    String roomCode;
}
