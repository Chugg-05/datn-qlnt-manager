package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.ContractTenantFilter;
import com.example.datn_qlnt_manager.dto.request.ContractTenant.AddTenantToContractRequest;
import com.example.datn_qlnt_manager.dto.request.ContractTenant.RepresentativeChangeRequest;
import com.example.datn_qlnt_manager.dto.response.contractTenant.ContractTenantDetailResponse;
import com.example.datn_qlnt_manager.dto.response.contractTenant.ContractTenantResponse;
import com.example.datn_qlnt_manager.entity.Contract;
import org.springframework.transaction.annotation.Transactional;

public interface ContractTenantService {


    PaginatedResponse<ContractTenantDetailResponse> getTenantsFromContract(
            String contractId,
            ContractTenantFilter filter,
            int page,
            int size
    );

    @Transactional
    ContractTenantResponse addTenantToContract(AddTenantToContractRequest request);

    void updateEndDateForContractTenants(Contract contract);

    @Transactional
    void changeRepresentative(RepresentativeChangeRequest request);

    void deleteTenantFromContract(String contractTenantId);

}
