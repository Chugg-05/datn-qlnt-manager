package com.example.datn_qlnt_manager.dto.filter;

import com.example.datn_qlnt_manager.common.RoomStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomFilter {
    RoomStatus status;
    Double maxPrice;
    Double minPrice;
    Double maxAcreage;
    Double minAcreage;
    Integer maximumPeople;
    String nameFloor;
    String buildingId;
    String floorId;
}
