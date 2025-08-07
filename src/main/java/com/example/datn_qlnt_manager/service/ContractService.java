package com.example.datn_qlnt_manager.service;

import java.util.List;

import jakarta.transaction.Transactional;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.ContractFilter;
import com.example.datn_qlnt_manager.dto.request.contract.ContractCreationRequest;
import com.example.datn_qlnt_manager.dto.request.contract.ContractUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.contract.ContractDetailResponse;
import com.example.datn_qlnt_manager.dto.response.contract.ContractResponse;
import com.example.datn_qlnt_manager.dto.statistics.ContractStatistics;

public interface ContractService {
    PaginatedResponse<ContractResponse> getPageAndSearchAndFilterTenantByUserId(
            ContractFilter filter, int page, int size);

    PaginatedResponse<ContractResponse> getContractWithStatusCancelByUserId(ContractFilter filter, int page, int size);

    ContractResponse updateContract(String contractId, ContractUpdateRequest request);

    @Transactional
    ContractResponse createContract(ContractCreationRequest request);

    ContractDetailResponse getContractDetailById(String contractId);

    List<ContractResponse> getAllContractsByUserId();

    void toggleContractStatusById(String contractId);

    ContractStatistics getContractStatisticsByUserId();

    void softDeleteContractById(String contractId);

    void deleteContractById(String contractId);

    PaginatedResponse<ContractResponse> getContractsOfCurrentTenant(ContractFilter filter, int page, int size);

    ContractResponse restoreContractById(String contractId);
}
