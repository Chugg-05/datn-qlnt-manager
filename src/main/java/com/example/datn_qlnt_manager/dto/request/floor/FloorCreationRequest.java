package com.example.datn_qlnt_manager.dto.request.floor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import com.example.datn_qlnt_manager.common.FloorType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FloorCreationRequest {

    @NotNull(message = "MAX_ROOM_INVALID")
    @Min(value = 1, message = "MAX_ROOM_AT_LEAST")
    @Max(value = 99, message = "MAX_ROOM_AT_MOST")
    Integer maximumRoom;

    @NotNull(message = "FLOOR_TYPE_INVALID")
    FloorType floorType;

    String buildingId;
    String descriptionFloor;
}
