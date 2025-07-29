package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.common.PaymentStatus;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.PaymentReceiptFilter;
import com.example.datn_qlnt_manager.dto.request.paymentReceipt.PaymentReceiptCreationRequest;
import com.example.datn_qlnt_manager.dto.request.paymentReceipt.PaymentReceiptStatusUpdateRequest;
import com.example.datn_qlnt_manager.dto.request.paymentReceipt.PaymentReceiptUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.paymentReceipt.PaymentReceiptResponse;
import com.example.datn_qlnt_manager.entity.Invoice;
import com.example.datn_qlnt_manager.entity.PaymentReceipt;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.PaymentReceiptMapper;
import com.example.datn_qlnt_manager.repository.InvoiceRepository;
import com.example.datn_qlnt_manager.repository.PaymentReceiptRepository;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentReceiptServiceImpl implements PaymentReceiptService {

     InvoiceRepository invoiceRepository;
     PaymentReceiptRepository paymentReceiptRepository;
     CodeGeneratorService codeGeneratorService;
     UserService userService;
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
                .receiptCode(codeGeneratorService.generateReceiptCode(invoice))
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
    public PaymentReceiptResponse updatePaymentReceipt(String id, PaymentReceiptUpdateRequest request) {
        PaymentReceipt receipt = paymentReceiptRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_RECEIPT_NOT_FOUND));

        // Không cho cập nhật nếu trạng thái đã xác nhận
        if (receipt.getPaymentStatus() == PaymentStatus.DA_THANH_TOAN) {
            throw new AppException(ErrorCode.PAYMENT_RECEIPT_CANNOT_BE_UPDATED);
        }

        if (request.getPaymentMethod() != null) {
            receipt.setPaymentMethod(request.getPaymentMethod());
        }

        if (request.getNote() != null) {
            receipt.setNote(request.getNote().isBlank() ? "Không có ghi chú!" : request.getNote());
        }
        receipt.setUpdatedAt(Instant.now());

        return paymentReceiptMapper.toResponse(paymentReceiptRepository.save(receipt));
    }

    @Override
    public void deletePaymentReceipt(String paymentReceiptId) {
        if (!paymentReceiptRepository.existsById(paymentReceiptId)) {
            throw new AppException(ErrorCode.PAYMENT_RECEIPT_NOT_FOUND);
        }
        paymentReceiptRepository.deleteById(paymentReceiptId);
    }

    @Override
    public PaymentReceiptResponse updatePaymentReceiptStatus(String paymentReceiptId, PaymentReceiptStatusUpdateRequest request) {
        PaymentReceipt receipt = paymentReceiptRepository.findById(paymentReceiptId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_RECEIPT_NOT_FOUND));

        if (receipt.getPaymentStatus() == PaymentStatus.DA_THANH_TOAN) {
            throw new AppException(ErrorCode.PAYMENT_RECEIPT_CANNOT_BE_UPDATED);
        }

        receipt.setPaymentStatus(request.getPaymentStatus());

        // Nếu trạng thái là ĐÃ XÁC NHẬN thì cập nhật PaymentDate = now
        if (PaymentStatus.DA_THANH_TOAN.equals(request.getPaymentStatus())) {
            receipt.setPaymentDate(LocalDateTime.now());
        }
        receipt.setUpdatedAt(Instant.now());
        return paymentReceiptMapper.toResponse(paymentReceiptRepository.save(receipt));
    }

    @Override
    public PaymentReceiptResponse confirmPaymentReceipt(String paymentReceiptId) {
        PaymentReceipt receipt = paymentReceiptRepository.findById(paymentReceiptId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_RECEIPT_NOT_FOUND));

        if (receipt.getPaymentStatus() != PaymentStatus.CHO_XAC_NHAN) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_STATUS_CHANGE);
        }

        receipt.setPaymentStatus(PaymentStatus.DA_THANH_TOAN);
        receipt.setPaymentDate(LocalDateTime.now());
        receipt.setUpdatedAt(Instant.now());

        paymentReceiptRepository.save(receipt);

        return paymentReceiptMapper.toResponse(receipt);
    }
}
