package com.example.datn_qlnt_manager.dto.response.room;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomCountResponse {
    //    String Id;
    String buildingId;
    Long getTotal;
    Long getTotalTrong;
    Long getTotalDangThue;
    Long getTotalDaDatCoc;
    Long getTotalDangBaoTri;
    Long getTotalChuaHoanThien;
    Long getTotalTamKhoa;
}
