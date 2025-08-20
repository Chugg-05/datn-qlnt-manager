package com.example.datn_qlnt_manager.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.ContractFilter;
import com.example.datn_qlnt_manager.dto.request.contract.ContractCreationRequest;
import com.example.datn_qlnt_manager.dto.request.contract.ContractUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.contract.ContractDetailResponse;
import com.example.datn_qlnt_manager.dto.response.contract.ContractResponse;
import com.example.datn_qlnt_manager.dto.statistics.ContractStatistics;
import com.example.datn_qlnt_manager.service.ContractService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Contract", description = "API Contract")
public class ContractController {

    ContractService contractService;

    @Operation(summary = "Lấy danh sách hợp đồng và lọc, tìm kiếm")
    @GetMapping
    public ApiResponse<List<ContractResponse>> getPageAndSearchAndFilterContract(
            @ModelAttribute ContractFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {

        PaginatedResponse<ContractResponse> result =
                contractService.getPageAndSearchAndFilterTenantByUserId(filter, page, size);

        return ApiResponse.<List<ContractResponse>>builder()
                .message("Get contracts successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Lấy danh sách hợp đồng và lọc, tìm kiếm với trạng thái đã hủy (status = DA_HUY)")
    @GetMapping("/cancel")
    public ApiResponse<List<ContractResponse>> getContractWithStatusCancel(
            @ModelAttribute ContractFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {

        PaginatedResponse<ContractResponse> result =
                contractService.getContractWithStatusCancelByUserId(filter, page, size);

        return ApiResponse.<List<ContractResponse>>builder()
                .message("Get cancelled contracts successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Tạo hợp đồng")
    @PostMapping
    public ApiResponse<ContractResponse> createContract(@Valid @RequestBody ContractCreationRequest request) {

        return ApiResponse.<ContractResponse>builder()
                .message("Contract created successfully")
                .data(contractService.createContract(request))
                .build();
    }

    @Operation(summary = "Update hợp đồng")
    @PutMapping("/{contractId}")
    public ApiResponse<ContractResponse> updateContract(
            @Valid @RequestBody ContractUpdateRequest request, @PathVariable("contractId") String contractId) {

        return ApiResponse.<ContractResponse>builder()
                .message("Contract updated successfully")
                .data(contractService.updateContract(contractId, request))
                .build();
    }

    @Operation(summary = "Lấy chi tiết hợp đồng")
    @GetMapping("/{contractId}")
    public ApiResponse<ContractDetailResponse> getContractDetail(@PathVariable String contractId) {
        ContractDetailResponse response = contractService.getContractDetail(contractId);

        return ApiResponse.<ContractDetailResponse>builder()
                .message("Contract detail retrieved successfully")
                .data(response)
                .build();
    }

    @Operation(summary = "Lấy danh sách hợp đồng theo user ID")
    @GetMapping("/all")
    public ApiResponse<List<ContractResponse>> getAllContractsByUserId() {
        List<ContractResponse> contracts = contractService.getAllContractsByUserId();
        return ApiResponse.<List<ContractResponse>>builder()
                .message("Contracts retrieved successfully")
                .data(contracts)
                .build();
    }

    @Operation(summary = "Kích hoạt hợp đồng")
    @PutMapping("/activate/{contractId}")
    public ApiResponse<String> contractActivation(@PathVariable("contractId") String contractId) {
        contractService.contractActivation(contractId);

        return ApiResponse.<String>builder()
                .message("Contract activation successfully")
                .data("Contract with ID " + contractId + " has been activated.")
                .build();
    }

    @Operation(summary = "Thống kê hợp đồng theo trạng thái")
    @GetMapping("/statistics")
    public ApiResponse<ContractStatistics> getContractStatistics() {

        return ApiResponse.<ContractStatistics>builder()
                .message("Contract statistics retrieved successfully")
                .data(contractService.getContractStatisticsByUserId())
                .build();
    }

    @Operation(summary = "Xóa mềm hợp đồng")
    @PutMapping("/soft/{contractId}")
    public ApiResponse<String> softDeleteContract(@PathVariable("contractId") String contractId) {
        contractService.softDeleteContractById(contractId);

        return ApiResponse.<String>builder()
                .message("Contract soft deleted successfully")
                .data("Contract with ID " + contractId + " has been soft deleted.")
                .build();
    }

    @Operation(summary = "Xoá hợp đồng")
    @DeleteMapping("/{contractId}")
    public ApiResponse<String> deleteContract(@PathVariable("contractId") String contractId) {
        contractService.deleteContractById(contractId);

        return ApiResponse.<String>builder()
                .message("Contract deleted successfully")
                .data("Contract with ID " + contractId + " has been deleted.")
                .build();
    }

    @Operation(summary = "Khách thuê - Xem danh sách, tìm kiếm, lọc hợp đồng của họ")
    @GetMapping("/my-contracts")
    public ApiResponse<List<ContractResponse>> getContractsOfCurrentTenant(
            @ModelAttribute ContractFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {

        PaginatedResponse<ContractResponse> result = contractService.getContractsOfCurrentTenant(filter, page, size);

        return ApiResponse.<List<ContractResponse>>builder()
                .message("Get contracts successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Khôi phục hợp đồng đã xóa")
    @PutMapping("/restore/{contractId}")
    public ApiResponse<ContractResponse> restoreContractById(@PathVariable("contractId") String contractId) {
        return ApiResponse.<ContractResponse>builder()
                .data(contractService.restoreContractById(contractId))
                .message("success")
                .build();
    }

    @Operation(summary = "Sửa nội dung hợp đồng")
    @PutMapping("/content/{contractId}")
    public ApiResponse<String> updateContent(@PathVariable("contractId") String contractId, @RequestBody String content) {
        return ApiResponse.<String>builder()
                .data(contractService.updateContent(contractId, content))
                .message("success")
                .build();
    }
}
