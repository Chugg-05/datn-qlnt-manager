package com.example.datn_qlnt_manager.dto.response.meter;

import java.util.List;

import com.example.datn_qlnt_manager.dto.response.IdAndName;
import com.example.datn_qlnt_manager.dto.response.IdNameAndType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateMeterInitResponse {

    List<IdNameAndType> services;
    List<IdAndName> rooms;
}
