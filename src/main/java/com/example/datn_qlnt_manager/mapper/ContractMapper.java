package com.example.datn_qlnt_manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.datn_qlnt_manager.dto.request.contract.ContractCreationRequest;
import com.example.datn_qlnt_manager.dto.request.contract.ContractUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.contract.ContractResponse;
import com.example.datn_qlnt_manager.entity.Contract;

@Mapper(componentModel = "spring")
public interface ContractMapper {

    @Mapping(target = "contractCode", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "status", constant = "CHO_KICH_HOAT")
    @Mapping(target = "contractTenants", ignore = true)
    @Mapping(target = "contractVehicles", ignore = true)
    Contract toContract(ContractCreationRequest request);

    @Mapping(source = "room.roomCode", target = "roomCode")
    ContractResponse toContractResponse(Contract contract);

    @Mapping(target = "contractCode", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "electricPrice", ignore = true)
    @Mapping(target = "waterPrice", ignore = true)
    @Mapping(target = "contractTenants", ignore = true)
    @Mapping(target = "contractVehicles", ignore = true)
    void updateContract(ContractUpdateRequest request, @MappingTarget Contract contract);
}
