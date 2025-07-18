package com.example.datn_qlnt_manager.dto.response.service;

import com.example.datn_qlnt_manager.common.ServiceCategory;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceBasicResponse {
    String id;
    String name;
    ServiceCategory category;
    String unit;
    String description;
}
