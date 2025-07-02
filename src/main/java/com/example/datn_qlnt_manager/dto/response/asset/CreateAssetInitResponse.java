package com.example.datn_qlnt_manager.dto.response.asset;

import com.example.datn_qlnt_manager.dto.response.IdAndName;
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
    List<IdAndName> rooms;
    List<IdAndName> buildings;
    List<IdAndName> floors;
    List<IdAndName> tenants;
}
