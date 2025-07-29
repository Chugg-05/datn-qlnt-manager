package com.example.datn_qlnt_manager.dto.request.assetRoom;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssetRoomCreationForBuildingRequest {

    @NotBlank(message = "ASSET_ID_REQUIRED")
    String assetId;

    @NotBlank(message = "BUILDING_ID_REQUIRED")
    String buildingId;
}
