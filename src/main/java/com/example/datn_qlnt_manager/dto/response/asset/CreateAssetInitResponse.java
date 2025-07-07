package com.example.datn_qlnt_manager.dto.response.asset;

import com.example.datn_qlnt_manager.dto.response.IdAndName;
import com.example.datn_qlnt_manager.dto.response.building.BuildingSelectResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateAssetInitResponse {

    List<IdAndName> assetTypes;
    List<BuildingSelectResponse> buildings;
}
