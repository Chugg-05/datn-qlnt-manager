package com.example.datn_qlnt_manager.dto.statistics;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomNoServiceStatisticResponse {
    String buildingId;
    Long totalRoomsWithoutService;
}
