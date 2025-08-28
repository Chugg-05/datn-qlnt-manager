package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.*;
import com.example.datn_qlnt_manager.entity.*;
import com.example.datn_qlnt_manager.repository.*;
import com.example.datn_qlnt_manager.service.AutoTaskService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AutoTaskServiceImpl implements AutoTaskService {
    ContractRepository contractRepository;
    RoomRepository roomRepository;
    InvoiceRepository invoiceRepository;
    PaymentReceiptRepository paymentReceiptRepository;
    DepositRepository depositRepository;
    TenantRepository tenantRepository;
    AssetRepository assetRepository;
    BuildingRepository buildingRepository;
    FloorRepository floorRepository;
    ServiceRepository serviceRepository;
    VehicleRepository vehicleRepository;

    @Override
    public void contractIsAboutToExpire() {
        LocalDate currentDate = LocalDate.now();
        LocalDate twoWeeksLater = currentDate.plusWeeks(2);

        List<Contract> contracts = contractRepository.findByEndDateBetween(currentDate, twoWeeksLater);

        for (Contract contract : contracts) {
            if (contract.getStatus() != ContractStatus.HIEU_LUC) {
                continue;
            }
            contract.setStatus(ContractStatus.SAP_HET_HAN);
            contract.setUpdatedAt(Instant.now());
            contractRepository.save(contract);
        }
    }

    @Override
    public void expiredContract() {
        LocalDate currentDate = LocalDate.now();

        List<Contract> contracts = contractRepository.findByEndDateBefore(currentDate);

        for (Contract contract : contracts) {
            if (!(contract.getStatus() == ContractStatus.HIEU_LUC
                    || contract.getStatus() == ContractStatus.SAP_HET_HAN)) {
                continue;
            }

            contract.setStatus(ContractStatus.KET_THUC_DUNG_HAN);
            contract.setUpdatedAt(Instant.now());
            contractRepository.save(contract);
        }
    }

    @Override
    public void roomOutOfContract() {
        LocalDate currentDate = LocalDate.now();

        List<Contract> contracts = contractRepository.findByEndDateBefore(currentDate);

        for (Contract contract : contracts) {
            if (!(contract.getStatus() == ContractStatus.KET_THUC_DUNG_HAN
                    || contract.getStatus() == ContractStatus.KET_THUC_CO_BAO_TRUOC
                    || contract.getStatus() == ContractStatus.TU_Y_HUY_BO)) {
                continue;
            }

            Room room = contract.getRoom();
            if (room != null) {
                room.setStatus(RoomStatus.TRONG);
                room.setUpdatedAt(Instant.now());
                roomRepository.save(room);
            }
        }
    }

    @Override
    public void expiredInvoice() {
        LocalDate currentDate = LocalDate.now();

        List<Invoice> expiredInvoices = invoiceRepository.findByPaymentDueDateBefore(currentDate);

        for (Invoice invoice : expiredInvoices) {
            invoice.setInvoiceStatus(InvoiceStatus.QUA_HAN);
            invoice.setUpdatedAt(Instant.now());
            invoiceRepository.save(invoice);

            List<PaymentReceipt> receipts = paymentReceiptRepository.findByInvoice(invoice);
            for (PaymentReceipt receipt : receipts) {
                receipt.setPaymentStatus(PaymentStatus.QUA_HAN);
                receipt.setUpdatedAt(Instant.now());
                paymentReceiptRepository.save(receipt);
            }
        }
    }

    @Override
    public void noDepositRefund() {
        List<Deposit> deposits = depositRepository.findAll();

        for (Deposit deposit : deposits) {
            Contract contract = deposit.getContract();

            if (contract.getStatus() == ContractStatus.TU_Y_HUY_BO) {
                deposit.setDepositStatus(DepositStatus.KHONG_TRA_COC);
                deposit.setDepositHoldDate(LocalDate.now());
                deposit.setNote("Tự ý hủy bỏ hợp đồng không báo trước, không trả cọc");
                deposit.setUpdatedAt(Instant.now());

                depositRepository.save(deposit);
            }
        }
    }

    @Override
    public void guestHasCheckedOut() {
        List<Tenant> tenants = tenantRepository.findAllByTenantStatus(TenantStatus.DANG_THUE);

        for (Tenant tenant : tenants) {
            boolean allContractsEnded = tenant.getContractTenants().stream()
                    .map(ContractTenant::getContract)
                    .allMatch(this::isContractEnded);

            if (allContractsEnded) {
                tenant.setPreviousTenantStatus(tenant.getTenantStatus());
                tenant.setTenantStatus(TenantStatus.DA_TRA_PHONG);
                tenantRepository.save(tenant);
            }
        }

    }

    @Override
    public void deleteCancelledTenants() {
        LocalDate cutoff = LocalDate.now().minusDays(30);

        List<Tenant> tenants = tenantRepository.findAllByTenantStatusAndDeletedAtBefore(
                TenantStatus.HUY_BO, cutoff
        );

        tenantRepository.deleteAll(tenants);
    }

    @Override
    public void deleteCancelledAssets() {
        LocalDate cutoff = LocalDate.now().minusDays(30);

        List<Asset> assets = assetRepository.findAllByAssetStatusAndDeletedAtBefore(
                AssetStatus.HUY, cutoff
        );

        assetRepository.deleteAll(assets);
    }

    @Override
    public void deleteCancelledBuildings() {
        LocalDate cutoff = LocalDate.now().minusDays(30);

        List<Building> buildings = buildingRepository.findAllByStatusAndDeletedAtBefore(
                BuildingStatus.HUY_HOAT_DONG, cutoff
        );

        buildingRepository.deleteAll(buildings);
    }

    @Override
    public void deleteCancelledContracts() {
        LocalDate  cutoff = LocalDate.now().minusDays(30);

        List<Contract> contracts = contractRepository.findAllByStatusAndDeletedAtBefore(
                ContractStatus.DA_HUY, cutoff
        );

        contractRepository.deleteAll(contracts);
    }

    @Override
    public void deleteCancelledFloors() {
        LocalDate cutoff = LocalDate.now().minusDays(30);

        List<Floor> floors = floorRepository.findAllByStatusAndDeletedAtBefore(
          FloorStatus.KHONG_SU_DUNG, cutoff
        );

        floorRepository.deleteAll(floors);
    }

    @Override
    public void deleteCancelledInvoices() {
        LocalDate cutoff = LocalDate.now().minusDays(30);

        List<Invoice> invoices = invoiceRepository.findAllByInvoiceStatusAndDeleteAtBefore(
                InvoiceStatus.HUY, cutoff
        );

        invoiceRepository.deleteAll(invoices);
    }

    @Override
    public void deleteCancelledRooms() {
        LocalDate cutoff = LocalDate.now().minusDays(30);

        List<Room> rooms = roomRepository.findAllByStatusAndDeleteAtBefore(
                RoomStatus.HUY_HOAT_DONG, cutoff
        );

        roomRepository.deleteAll(rooms);
    }

    @Override
    public void deleteCancelledServices() {
        LocalDate cutoff = LocalDate.now().minusDays(30);

        List<com.example.datn_qlnt_manager.entity.Service> services = serviceRepository.findAllByStatusAndDeleteAtBefore(
                ServiceStatus.KHONG_SU_DUNG, cutoff
        );

        serviceRepository.deleteAll(services);
    }

    @Override
    public void deleteCancelledVehicle() {
        LocalDate cutoff = LocalDate.now().minusDays(30);

        List<Vehicle> vehicles = vehicleRepository.findAllByVehicleStatusAndDeleteAtBefore(
                VehicleStatus.KHONG_SU_DUNG, cutoff
        );

        vehicleRepository.deleteAll(vehicles);
    }

    private boolean isContractEnded(Contract contract) {
        return contract.getStatus() == ContractStatus.KET_THUC_DUNG_HAN
                || contract.getStatus() == ContractStatus.KET_THUC_CO_BAO_TRUOC
                || contract.getStatus() == ContractStatus.TU_Y_HUY_BO;
    }
}
