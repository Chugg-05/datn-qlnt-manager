package com.example.datn_qlnt_manager.mapper;

import com.example.datn_qlnt_manager.dto.request.contract.ContractCreationRequest;
import com.example.datn_qlnt_manager.dto.request.contract.ContractUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.contract.ContractResponse;
import com.example.datn_qlnt_manager.entity.Contract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ContractMapper {

    @Mapping(target = "contractCode", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "tenants", ignore = true)
    @Mapping(target = "status", constant = "HIEU_LUC")
    Contract toContract(ContractCreationRequest request);

    @Mapping(source = "room.roomCode", target = "roomCode")
    ContractResponse toContractResponse(Contract contract);

    @Mapping(target = "contractCode", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "deposit", ignore = true)
    @Mapping(target = "tenants", ignore = true)
    void updateContract(ContractUpdateRequest request, @MappingTarget Contract contract);

}
