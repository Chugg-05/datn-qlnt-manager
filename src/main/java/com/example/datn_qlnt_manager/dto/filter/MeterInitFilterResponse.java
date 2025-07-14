package com.example.datn_qlnt_manager.dto.filter;

import com.example.datn_qlnt_manager.dto.response.IdAndName;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeterInitFilterResponse {

    List<IdAndName> rooms;
}
