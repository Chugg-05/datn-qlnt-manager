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

    @Override
    public void updateContractStatus() {
        LocalDate currentDate = LocalDate.now();
        LocalDate twoWeeksLater = currentDate.plusWeeks(2);

        List<Contract> contracts = contractRepository.findByEndDateBetween(currentDate, twoWeeksLater);

        for (Contract contract : contracts) {
            contract.setStatus(ContractStatus.SAP_HET_HAN);
            contract.setUpdatedAt(Instant.now());
            contractRepository.save(contract);
        }
    }

    @Override
    public void finalizeExpiredContracts() {
        LocalDate currentDate = LocalDate.now();

        List<Contract> contracts = contractRepository.findByEndDateBefore(currentDate);

        for (Contract contract : contracts) {
            contract.setStatus(ContractStatus.KET_THUC_DUNG_HAN);
            contract.setUpdatedAt(Instant.now());
            contractRepository.save(contract);

            Room room = contract.getRoom();
            if (room != null) {
                room.setStatus(RoomStatus.TRONG);
                room.setUpdatedAt(Instant.now());
                roomRepository.save(room);
            }
        }
    }

    @Override
    public void updateExpiredInvoices() {
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
    public void updateDepositsIfContractCancelled() {
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
}
