package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.dto.request.contractVehicle.AddVehicleToContractRequest;
import com.example.datn_qlnt_manager.dto.response.contractVehicle.ContractVehicleResponse;
import com.example.datn_qlnt_manager.entity.Contract;
import com.example.datn_qlnt_manager.entity.ContractVehicle;
import com.example.datn_qlnt_manager.entity.Vehicle;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.repository.ContractRepository;
import com.example.datn_qlnt_manager.repository.ContractVehicleRepository;
import com.example.datn_qlnt_manager.repository.VehicleRepository;
import com.example.datn_qlnt_manager.service.ContractVehicleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContractVehicleServiceImpl implements ContractVehicleService {
    ContractVehicleRepository contractVehicleRepository;
    ContractRepository contractRepository;
    VehicleRepository vehicleRepository;

    @Transactional
    @Override
    public ContractVehicleResponse addVehicleToContract(AddVehicleToContractRequest request) {
        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new AppException(ErrorCode.VEHICLE_NOT_FOUND));

        boolean isActive = contractVehicleRepository.existsActiveContractForVehicle(vehicle.getId(), LocalDate.now());
        if (isActive) {
            throw new AppException(ErrorCode.VEHICLE_ALREADY_IN_ACTIVE_CONTRACT);
        }

        ContractVehicle contractVehicle = ContractVehicle.builder()
                .contract(contract)
                .vehicle(vehicle)
                .startDate(LocalDate.now())
                .endDate(contract.getEndDate())
                .build();

        contractVehicle.setCreatedAt(Instant.now());
        contractVehicle.setUpdatedAt(Instant.now());

        ContractVehicle response = contractVehicleRepository.save(contractVehicle);

        return ContractVehicleResponse.builder()
                .id(response.getId())
                .contractId(response.getContract().getId())
                .tenantId(vehicle.getTenant().getId())
                .vehicleId(response.getVehicle().getId())
                .vehicleType(vehicle.getVehicleType())
                .licensePlate(vehicle.getLicensePlate())
                .vehicleStatus(vehicle.getVehicleStatus())
                .registrationDate(vehicle.getRegistrationDate())
                .startDate(response.getStartDate())
                .endDate(response.getEndDate())
                .description(vehicle.getDescribe())
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .build();
    }

    @Override
    public void deleteVehicleFromContract(String contractVehicleId) {
        ContractVehicle contractVehicle = contractVehicleRepository.findById(contractVehicleId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_VEHICLE_NOT_FOUND));

        contractVehicleRepository.deleteById(contractVehicle.getId());
    }

}
