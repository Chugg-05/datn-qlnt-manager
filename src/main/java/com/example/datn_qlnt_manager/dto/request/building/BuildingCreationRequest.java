package com.example.datn_qlnt_manager.dto.request.building;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.example.datn_qlnt_manager.common.BuildingType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildingCreationRequest {

    String userId;

    @NotBlank(message = "INVALID_BUILDING_NAME_BLANK")
    String buildingName;

    @NotBlank(message = "INVALID_ADDRESS_BLANK")
    String address;

    @NotNull(message = "INVALID_ACTUAL_NUMBER_OF_FLOORS_BLANK")
    @Min(value = 1, message = "ACTUAL_FLOOR_NUMBER_IS_INVALID")
    Integer actualNumberOfFloors;

    @NotNull(message = "INVALID_NUMBER_OF_FLOORS_FOR_RENT_BLANK")
    @Min(value = 1, message = "INVALID_RENTAL_FLOOR_NUMBER")
    Integer numberOfFloorsForRent;

    @NotNull(message = "INVALID_BUILDING_TYPE_BLANK")
    BuildingType buildingType;

    String description;
}