package com.example.datn_qlnt_manager.dto.request.assetRoom;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssetRoomCreationForAssetRequest {

    @NotBlank(message = "ASSET_ID_REQUIRED")
    String assetId;

    @NotEmpty(message = "ROOM_ID_REQUIRED")
    List<String> roomIds;
}
