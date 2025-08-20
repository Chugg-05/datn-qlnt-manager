package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.request.contractVehicle.AddVehicleToContractRequest;
import com.example.datn_qlnt_manager.dto.response.contractVehicle.ContractVehicleResponse;
import org.springframework.transaction.annotation.Transactional;

public interface ContractVehicleService {
    @Transactional
    ContractVehicleResponse addVehicleToContract(AddVehicleToContractRequest request);

    void deleteVehicleFromContract(String contractVehicleId);
}
