package com.example.datn_qlnt_manager.dto.request.room;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class RoomDeleteRequest {
    @NotNull
    String floorId;

    @NotBlank
    String roomCode;

}
