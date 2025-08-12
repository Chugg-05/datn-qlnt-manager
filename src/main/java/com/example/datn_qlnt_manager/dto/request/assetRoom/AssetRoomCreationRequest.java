package com.example.datn_qlnt_manager.dto.request.assetRoom;

import com.example.datn_qlnt_manager.common.AssetBeLongTo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssetRoomCreationRequest {
    @NotNull(message = "ASSET_BELONG_TO_REQUIRED")
    AssetBeLongTo assetBeLongTo;

    @NotBlank(message = "ROOM_NOT_FOUND")
    String roomId;

    String assetId;

    String assetName;

    Integer quantity;

    @DecimalMin(value = "0.0", inclusive = false, message = "PRICE_MUST_BE_POSITIVE")
    BigDecimal price;

    String description;
}
