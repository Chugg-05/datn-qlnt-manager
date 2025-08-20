package com.example.datn_qlnt_manager.service.implement;

import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.entity.*;
import com.example.datn_qlnt_manager.service.strategy.*;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CodeGeneratorService {
    BuildingCodeStrategy buildingCodeStrategy;
    FloorNameStrategy floorNameStrategy;
    RoomCodeStrategy roomCodeStrategy;
    TenantCodeStrategy tenantCodeStrategy;
    ContractCodeStrategy contractCodeStrategy;
    InvoiceCodeStrategy invoiceCodeStrategy;
    JobCodeStrategy jobCodeStrategy;
    ReceiptCodeStrategy receiptCodeStrategy;

    public String generateBuildingCode(User user) {
        return buildingCodeStrategy.generate(user);
    }

    public String generateFloorName(Building building) {
        return floorNameStrategy.generate(building);
    }

    public String generateRoomCode(Building building, Floor floor) {
        return roomCodeStrategy.generate(building, floor);
    }

    public String generateTenantCode(User user) {
        return tenantCodeStrategy.generate(user);
    }

    public String generateContractCode(Room room) {
        return contractCodeStrategy.generate(room);
    }

    public String generateInvoiceCode(Room room, int month, int year) {
        return invoiceCodeStrategy.generate(room, month, year);
    }

    public String generateJobCode(Building building) {
        return jobCodeStrategy.generate(building);
    }

    public String generateReceiptCode() {
        return receiptCodeStrategy.generateReceiptCode();
    }
}
