package com.example.datn_qlnt_manager.dto.response.serviceRoom;

import com.example.datn_qlnt_manager.dto.response.IdAndName;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateRoomServiceInitResponse {

    List<IdAndName> rooms;
    List<IdAndName> services;
}
