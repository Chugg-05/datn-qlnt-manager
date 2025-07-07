package com.example.datn_qlnt_manager.dto.request.room;

import jakarta.validation.constraints.NotNull;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.common.RoomType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomCreationRequest {

    @NotNull
    String floorId;

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
