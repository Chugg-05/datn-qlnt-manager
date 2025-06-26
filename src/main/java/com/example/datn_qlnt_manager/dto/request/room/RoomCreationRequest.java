package com.example.datn_qlnt_manager.dto.request.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.common.RoomType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomCreationRequest {

    @NotNull
    String floorId;

    @NotBlank
    String roomCode;

    @NotNull
    Double acreage;

    @NotNull
    Double price;

    @NotNull
    @Builder.Default
    Long maximumPeople = 0L;

    @Builder.Default
    RoomType roomType = RoomType.DON;

    @NotNull
    RoomStatus status;

    String description;
}
