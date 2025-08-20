package com.example.datn_qlnt_manager.dto.response.contract;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import com.example.datn_qlnt_manager.common.ContractStatus;
import com.example.datn_qlnt_manager.dto.response.asset.AssetLittleResponse;
import com.example.datn_qlnt_manager.dto.response.service.ServiceLittleResponse;
import com.example.datn_qlnt_manager.dto.response.tenant.TenantLittleResponse;
import com.example.datn_qlnt_manager.dto.response.vehicle.VehicleBasicResponse;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractDetailResponse {
    String id;
    String roomId;
    String contractCode;
    String roomCode;
    String nameManager;
    String phoneNumberManager;
    String nameUser;
    String emailUser;
    String phoneNumberUser;
    String identityCardUser;
    String addressUser;
    LocalDate startDate;
    LocalDate endDate;
    BigDecimal deposit;
    BigDecimal roomPrice;
    String buildingAddress;
    ContractStatus status;
    BigDecimal electricPrice;
    BigDecimal waterPrice;
    List<TenantLittleResponse> tenants;
    List<AssetLittleResponse> assets;
    List<ServiceLittleResponse> services;
    List<VehicleBasicResponse> vehicles;
    Instant createdAt;
    Instant updatedAt;
    String content;

    public ContractDetailResponse(
            String id,
            String roomId,
            String contractCode,
            String roomCode,
            String nameManager,
            String phoneNumberManager,
            String nameUser,
            String emailUser,
            String phoneNumberUser,
            String identityCardUser,
            String addressUser,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal deposit,
            BigDecimal roomPrice,
            String buildingAddress,
            ContractStatus status,
            BigDecimal electricPrice,
            BigDecimal waterPrice,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.roomId = roomId;
        this.contractCode = contractCode;
        this.roomCode = roomCode;
        this.nameManager = nameManager;
        this.phoneNumberManager = phoneNumberManager;
        this.nameUser = nameUser;
        this.emailUser = emailUser;
        this.phoneNumberUser = phoneNumberUser;
        this.identityCardUser = identityCardUser;
        this.addressUser = addressUser;
        this.startDate = startDate;
        this.endDate = endDate;
        this.deposit = deposit;
        this.roomPrice = roomPrice;
        this.buildingAddress = buildingAddress;
        this.status = status;
        this.electricPrice = electricPrice;
        this.waterPrice = waterPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
