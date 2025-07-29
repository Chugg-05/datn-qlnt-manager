package com.example.datn_qlnt_manager.dto.request.assetRoom;

import com.example.datn_qlnt_manager.common.AssetStatus;
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
public class AssetRoomUpdateRequest {

    @NotBlank(message = "ASSET_CANNOT_BE_BLANK")
    String assetName;

    @NotNull(message = "PRICE_CANNOT_BE_NULL")
    @DecimalMin(value = "0.0", inclusive = false, message = "PRICE_MUST_BE_POSITIVE")
    BigDecimal price;

    @NotNull(message = "ASSET_STATUS_CANNOT_BE_NULL")
    AssetStatus assetStatus;

    String description;
}
