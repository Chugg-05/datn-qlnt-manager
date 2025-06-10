package com.example.datn_qlnt_manager.dto.response;

import com.example.datn_qlnt_manager.common.FloorStatus;

import java.time.LocalDate;

public class FloorCreationResponse {
    private String id;
    private String floorName;
    private FloorStatus status;
    private String description;
    private String buildingId;
    private String buildingName;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
