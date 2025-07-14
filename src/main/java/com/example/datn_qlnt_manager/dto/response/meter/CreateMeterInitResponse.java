package com.example.datn_qlnt_manager.dto.response.meter;


import com.example.datn_qlnt_manager.dto.response.IdAndName;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateMeterInitResponse {

    List<IdAndName> services;
    List<IdAndName> rooms;
}
