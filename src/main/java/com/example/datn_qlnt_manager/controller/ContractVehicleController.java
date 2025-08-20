package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.request.contractVehicle.AddVehicleToContractRequest;
import com.example.datn_qlnt_manager.dto.response.contractVehicle.ContractVehicleResponse;
import com.example.datn_qlnt_manager.service.ContractVehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/contract-vehicles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Contract Vehicle", description = "API Contract Vehicle")
public class ContractVehicleController {
    ContractVehicleService contractVehicleService;

    @Operation(summary = "Add vehicle to contract")
    @PostMapping
    public ApiResponse<ContractVehicleResponse> addVehicleToContract(@RequestBody AddVehicleToContractRequest request) {

        return ApiResponse.<ContractVehicleResponse>builder()
                .message("Vehicle added to contract successfully")
                .data(contractVehicleService.addVehicleToContract(request))
                .build();
    }

    @Operation(summary = "Delete vehicle from contract")
    @DeleteMapping("/{contractVehicleId}")
    public ApiResponse<String> deleteVehicleFromContract(@PathVariable("contractVehicleId") String contractVehicleId) {
        contractVehicleService.deleteVehicleFromContract(contractVehicleId);

        return ApiResponse.<String>builder()
                .message("Vehicle removed from contract successfully")
                .data("Vehicle with ID " + contractVehicleId + " has been removed from the contract.")
                .build();
    }
}
