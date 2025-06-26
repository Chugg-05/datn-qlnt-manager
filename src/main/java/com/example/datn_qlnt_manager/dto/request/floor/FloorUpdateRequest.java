package com.example.datn_qlnt_manager.dto.request.floor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.example.datn_qlnt_manager.common.FloorStatus;
import com.example.datn_qlnt_manager.common.FloorType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FloorUpdateRequest {
    @NotBlank(message = "FLOOR_NAME_INVALID")
    String nameFloor;

    @NotNull(message = "MAX_ROOM_INVALID")
    @Min(value = 1, message = "MAX_ROOM_AT_LEAST")
    Integer maximumRoom;

    @NotNull(message = "FLOOR_TYPE_INVALID")
    FloorType floorType;

    @NotNull(message = "STATUS_TYPE_INVALID")
    FloorStatus status;

    String description;
}
