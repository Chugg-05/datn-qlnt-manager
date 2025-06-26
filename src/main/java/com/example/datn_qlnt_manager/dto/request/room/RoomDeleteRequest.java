package com.example.datn_qlnt_manager.dto.request.room;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class RoomDeleteRequest {
    @NotNull
    String floorId;
    @NotBlank
    String roomCode;

}
