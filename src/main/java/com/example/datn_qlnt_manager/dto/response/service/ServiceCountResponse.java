package com.example.datn_qlnt_manager.dto.response.service;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceCountResponse {
    Long getTotal;
    Long getTotalHoatDong;
    Long getTotalTamKhoa;
}
