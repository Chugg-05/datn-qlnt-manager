package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.*;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.PaymentReceiptFilter;
import com.example.datn_qlnt_manager.dto.request.paymentReceipt.*;
import com.example.datn_qlnt_manager.dto.response.paymentReceipt.PaymentBatchResponse;
import com.example.datn_qlnt_manager.dto.response.paymentReceipt.PaymentMethodResponse;
import com.example.datn_qlnt_manager.dto.response.paymentReceipt.PaymentReceiptResponse;
import com.example.datn_qlnt_manager.dto.response.paymentReceipt.RejectPaymentResponse;
import com.example.datn_qlnt_manager.entity.Invoice;
import com.example.datn_qlnt_manager.entity.PaymentReceipt;
import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.InvoiceMapper;
import com.example.datn_qlnt_manager.mapper.PaymentReceiptMapper;
import com.example.datn_qlnt_manager.repository.InvoiceRepository;
import com.example.datn_qlnt_manager.repository.PaymentReceiptRepository;
import com.example.datn_qlnt_manager.service.EmailService;
import com.example.datn_qlnt_manager.service.PaymentReceiptService;
import com.example.datn_qlnt_manager.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentReceiptServiceImpl implements PaymentReceiptService {

     InvoiceRepository invoiceRepository;
     PaymentReceiptRepository paymentReceiptRepository;
     InvoiceMapper invoiceMapper;
     CodeGeneratorService codeGeneratorService;
     UserService userService;
     EmailService emailService;
     PaymentReceiptMapper paymentReceiptMapper;

    @Override
    public PaymentReceiptResponse createPaymentReceipt(PaymentReceiptCreationRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));

        boolean exists = paymentReceiptRepository.existsByInvoiceId(request.getInvoiceId());
        if (exists) {
            throw new AppException(ErrorCode.PAYMENT_RECEIPT_ALREADY_EXISTS);
        }

        PaymentReceipt receipt = PaymentReceipt.builder()
                .invoice(invoice)
                .receiptCode(codeGeneratorService.generateReceiptCode())
                .amount(invoice.getTotalAmount())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.CHO_XAC_NHAN)
                .collectedBy(userService.getCurrentUser().getFullName())
                .note(request.getNote() != null && !request.getNote().isBlank() ? request.getNote() : "Không có ghi chú!")
                .build();

                receipt.setCreatedAt(Instant.now());
                receipt.setUpdatedAt(Instant.now());
        return paymentReceiptMapper.toResponse(paymentReceiptRepository.save(receipt));
    }

    @Override
    public PaginatedResponse<PaymentReceiptResponse> filterPaymentReceiptsByUserId(
            PaymentReceiptFilter filter, int page, int size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        Page<PaymentReceipt> paymentPage = paymentReceiptRepository.filterPaymentReceipts(
                filter.getQuery(),
                filter.getPaymentStatus(),
                filter.getPaymentMethod(),
                filter.getFromAmount(),
                filter.getToAmount(),
                filter.getFromDate(),
                filter.getToDate(),
                pageable
        );

        List<PaymentReceiptResponse> responses = paymentPage
                .getContent()
                .stream()
                .map(paymentReceiptMapper::toResponse)
                .collect(Collectors.toList());

        Pagination pagination = Pagination.builder()
                .total(paymentPage.getTotalElements())
                .count(paymentPage.getNumberOfElements())
                .perPage(size)
                .currentPage(page)
                .totalPages(paymentPage.getTotalPages())
                .build();

        Meta<?> meta = Meta.builder()
                .pagination(pagination)
                .build();

        return PaginatedResponse.<PaymentReceiptResponse>builder()
                .data(responses)
                .meta(meta)
                .build();
    }


    @Override
    public void deletePaymentReceipt(String paymentReceiptId) {
        if (!paymentReceiptRepository.existsById(paymentReceiptId)) {
            throw new AppException(ErrorCode.PAYMENT_RECEIPT_NOT_FOUND);
        }
        paymentReceiptRepository.deleteById(paymentReceiptId);
    }

    @Transactional
    @Override
    public PaymentBatchResponse generateMonthlyPaymentRequests() {
        YearMonth current = YearMonth.now();

        List<Invoice> invoices = invoiceRepository.findAllByStatusAndMonth(
                InvoiceStatus.CHUA_THANH_TOAN,
                current.getMonthValue(),
                current.getYear()
        );

        if (invoices.isEmpty()) {
            throw new AppException(ErrorCode.NO_PENDING_INVOICES);
        }

        int created = 0;
        Set<String> notifiedEmails = new HashSet<>();

        for (Invoice invoice : invoices) {

            boolean exists = paymentReceiptRepository.existsByInvoiceId(invoice.getId());
            if (exists) {
                continue;
            }

            //Tạo phiếu thanh toán
            PaymentReceipt receipt = PaymentReceipt.builder()
                    .invoice(invoice)
                    .receiptCode(codeGeneratorService.generateReceiptCode())
                    .amount(invoice.getTotalAmount())
                    .paymentMethod(PaymentMethod.CHON_PHUONG_THUC)
                    .paymentStatus(PaymentStatus.CHO_THANH_TOAN)
                    .collectedBy(userService.getCurrentUser().getFullName())
                    .paymentDate(null)
                    .note("Phiếu thanh toán cho hóa đơn " + invoice.getInvoiceCode() + " đã được tạo")
                    .build();

            paymentReceiptRepository.save(receipt);
            created++;

            //Cập nhật trạng thái hóa đơn
            invoice.setInvoiceStatus(InvoiceStatus.CHO_THANH_TOAN);
            invoice.setUpdatedAt(Instant.now());
            invoiceRepository.save(invoice);

            //Gửi email tới tất cả khách thuê có tài khoản
            invoice.getContract().getTenants().forEach(tenant -> {
                User user = tenant.getUser();
                if (user != null && user.getEmail() != null) {
                    emailService.sendPaymentNotificationToTenant(
                            user.getEmail(),
                            user.getFullName(),
                            invoice,
                            receipt
                    );
                    notifiedEmails.add(user.getEmail());
                }
            });
        }

        if (created == 0) {
            throw new AppException(ErrorCode.ALL_INVOICES_ALREADY_HAVE_RECEIPTS);
        }

        return PaymentBatchResponse.builder()
                .totalInvoices(invoices.size())
                .createdReceipts(created)
                .notifiedUsers(notifiedEmails.size())
                .build();
    }

    @Transactional
    @Override
    public PaymentMethodResponse confirmPaymentMethod(String receiptId, PaymentMethodRequest request) {
        PaymentReceipt receipt = paymentReceiptRepository.findById(receiptId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_RECEIPT_NOT_FOUND));

        if (request.getPaymentMethod() == PaymentMethod.CHON_PHUONG_THUC) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_METHOD);
        }

        switch (request.getPaymentMethod()) {
            case TIEN_MAT -> {
                receipt.setPaymentMethod(request.getPaymentMethod());
                receipt.setPaymentStatus(PaymentStatus.CHO_XAC_NHAN);
                receipt.setUpdatedAt(Instant.now());
                paymentReceiptRepository.save(receipt);
                emailService.notifyOwnerForCashReceipt(receipt, invoiceMapper.getRepresentativeName(receipt.getInvoice()));
            }
            case CHUYEN_KHOAN, VNPAY, ZALOPAY, MOMO -> throw new AppException(ErrorCode.NOT_SUPPORTED_YET);

            default -> throw new AppException(ErrorCode.INVALID_PAYMENT_METHOD);
        }

        return PaymentMethodResponse.builder()
                .id(receipt.getId())
                .paymentMethod(receipt.getPaymentMethod())
                .build();
    }

    @Transactional
    @Override
    public RejectPaymentResponse rejectPaymentReceipt(String receiptId, RejectPaymentRequest request) {
        PaymentReceipt receipt = paymentReceiptRepository.findById(receiptId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_RECEIPT_NOT_FOUND));

        if (receipt.getPaymentStatus() != PaymentStatus.CHO_THANH_TOAN) {
            throw new AppException(ErrorCode.CANNOT_REFUSE_PAYMENTS);
        }

        receipt.setPaymentStatus(PaymentStatus.TU_CHOI);
        receipt.setNote(request.getReason());
        receipt.setUpdatedAt(Instant.now());
        paymentReceiptRepository.save(receipt);

        Invoice invoice = receipt.getInvoice();
        invoice.setInvoiceStatus(InvoiceStatus.CHUA_THANH_TOAN);
        invoice.setUpdatedAt(Instant.now());
        invoiceRepository.save(invoice);

        String representativeName = invoiceMapper.getRepresentativeName(receipt.getInvoice());
        emailService.notifyOwnerRejectedReceipt(receipt, representativeName);

        return RejectPaymentResponse.builder()
                .id(receipt.getId())
                .paymentStatus(receipt.getPaymentStatus())
                .build();
    }

    @Transactional
    @Override
    public void confirmCashPayment(String receiptId) {
        PaymentReceipt receipt = paymentReceiptRepository.findById(receiptId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_RECEIPT_NOT_FOUND));

        if (receipt.getPaymentMethod() != PaymentMethod.TIEN_MAT) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_METHOD);
        }

        if (receipt.getPaymentStatus() != PaymentStatus.CHO_XAC_NHAN) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_STATUS);
        }


        receipt.setPaymentStatus(PaymentStatus.DA_THANH_TOAN);
        receipt.setPaymentDate(LocalDateTime.now());
        receipt.setUpdatedAt(Instant.now());
        paymentReceiptRepository.save(receipt);

        Invoice invoice = receipt.getInvoice();
        invoice.setInvoiceStatus(InvoiceStatus.DA_THANH_TOAN);
        invoice.setUpdatedAt(Instant.now());
        invoiceRepository.save(invoice);

        emailService.notifyTenantPaymentConfirmed(receipt);
    }


}
