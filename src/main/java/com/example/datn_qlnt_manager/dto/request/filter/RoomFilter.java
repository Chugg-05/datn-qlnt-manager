package com.example.datn_qlnt_manager.dto.request.filter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomFilter {

    String status;
    Double maxPrice;
    Double mixPrice;
    Double maxAcreage;
    Double minAcreage;
    Integer maximumPeople;
    String nameFloor;

}
