package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.ContractStatus;
import com.example.datn_qlnt_manager.common.InvoiceStatus;
import com.example.datn_qlnt_manager.common.PaymentStatus;
import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.entity.Contract;
import com.example.datn_qlnt_manager.entity.Invoice;
import com.example.datn_qlnt_manager.entity.PaymentReceipt;
import com.example.datn_qlnt_manager.entity.Room;
import com.example.datn_qlnt_manager.repository.ContractRepository;
import com.example.datn_qlnt_manager.repository.InvoiceRepository;
import com.example.datn_qlnt_manager.repository.PaymentReceiptRepository;
import com.example.datn_qlnt_manager.repository.RoomRepository;
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
}
