package com.example.datn_qlnt_manager.dto.request.electricityWaterMeter;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeterDeleteRequest {

    @NotBlank(message = "ID must not be blank")
    String id;

    @NotBlank(message = "Room code must not be blank")
    String roomCode;
}
