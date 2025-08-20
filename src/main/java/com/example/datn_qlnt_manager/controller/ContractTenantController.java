package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.ContractTenantFilter;
import com.example.datn_qlnt_manager.dto.request.ContractTenant.AddTenantToContractRequest;
import com.example.datn_qlnt_manager.dto.request.ContractTenant.RepresentativeChangeRequest;
import com.example.datn_qlnt_manager.dto.response.contractTenant.ContractTenantDetailResponse;
import com.example.datn_qlnt_manager.dto.response.contractTenant.ContractTenantResponse;
import com.example.datn_qlnt_manager.service.ContractTenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/contract-tenants")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Contract Tenant", description = "API Contract Tenant")
public class ContractTenantController {
    ContractTenantService contractTenantService;

    @Operation(summary = "Get tenants from a contract")
    @GetMapping("/{contractId}")
    public ApiResponse<List<ContractTenantDetailResponse>> getTenantsFromContract(
            @PathVariable("contractId") String contractId,
            @ModelAttribute ContractTenantFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {

        PaginatedResponse<ContractTenantDetailResponse> result =
                contractTenantService.getTenantsFromContract(contractId, filter, page, size);

        return ApiResponse.<List<ContractTenantDetailResponse>>builder()
                .message("Get contracts successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Add a tenant for a contract")
    @PostMapping
    public ApiResponse<ContractTenantResponse> addTenantToContract(@RequestBody AddTenantToContractRequest request) {

        return ApiResponse.<ContractTenantResponse>builder()
                .message("Tenant added to contract successfully")
                .data(contractTenantService.addTenantToContract(request))
                .build();
    }

    @Operation(summary = "Change the representative of a tenant in a contract")
    @PutMapping("/representative")
    public ApiResponse<String> changeRepresentative(@RequestBody RepresentativeChangeRequest request) {

        contractTenantService.changeRepresentative(request);

        return ApiResponse.<String>builder()
                .message("Representative changed successfully")
                .data("The representative for tenant with ID " + request.getTenantId() + " has been updated.")
                .build();
    }

    @Operation(summary = "Delete a tenant from a contract")
    @DeleteMapping("/{contractTenantId}")
    public ApiResponse<String> deleteTenantFromContract(@PathVariable("contractTenantId") String contractTenantId) {

        contractTenantService.deleteTenantFromContract(contractTenantId);
        
        return ApiResponse.<String>builder()
                .message("Tenant removed from contract successfully")
                .data("Tenant with ID " + contractTenantId + " has been removed from the contract.")
                .build();
    }
}
