package com.example.datn_qlnt_manager.dto.response.floor;

import java.util.List;

import com.example.datn_qlnt_manager.dto.response.room.RoomSelectResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FloorSelectResponse {

    private String id;
    private String name;
    List<RoomSelectResponse> rooms;
}
