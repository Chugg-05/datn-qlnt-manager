package com.example.datn_qlnt_manager.dto.response.room;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomCountResponse {
    Long getTotalInUse;
    Long getTotalDangThue;
    Long getTotalDatCoc;
}
