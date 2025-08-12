package com.example.datn_qlnt_manager.dto.request.assetRoom;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssetRoomCreationForRoomRequest {

    @NotBlank(message = "ROOM_ID_REQUIRED")
    String roomId;

    @NotEmpty(message = "ASSET_ID_REQUIRED")
    List<String> assetIds;
}
