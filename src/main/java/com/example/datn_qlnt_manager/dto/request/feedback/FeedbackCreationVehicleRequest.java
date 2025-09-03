package com.example.datn_qlnt_manager.dto.request.feedback;


import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackCreationVehicleRequest {

    @NotBlank(message = "ROOM_NOT_FOUND")
    String roomId;

    String reason;
}
