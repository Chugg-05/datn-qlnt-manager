package com.example.datn_qlnt_manager.dto.response.serviceRoom;

import java.util.List;

import com.example.datn_qlnt_manager.dto.response.IdAndName;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateRoomServiceInitResponse {

    List<IdAndName> rooms;
    List<IdAndName> services;
}
