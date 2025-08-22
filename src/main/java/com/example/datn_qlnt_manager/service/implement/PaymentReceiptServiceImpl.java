package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.*;
import com.example.datn_qlnt_manager.configuration.VnpayConfig;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.PaymentReceiptFilter;
import com.example.datn_qlnt_manager.dto.request.paymentReceipt.*;
import com.example.datn_qlnt_manager.dto.response.paymentReceipt.PaymentBatchResponse;
import com.example.datn_qlnt_manager.dto.response.paymentReceipt.PaymentMethodResponse;
import com.example.datn_qlnt_manager.dto.response.paymentReceipt.PaymentReceiptResponse;

import com.example.datn_qlnt_manager.entity.*;

import com.example.datn_qlnt_manager.dto.response.paymentReceipt.RejectPaymentResponse;
import com.example.datn_qlnt_manager.entity.Invoice;
import com.example.datn_qlnt_manager.entity.PaymentReceipt;
import com.example.datn_qlnt_manager.entity.User;

import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.PaymentReceiptMapper;
import com.example.datn_qlnt_manager.repository.InvoiceRepository;
import com.example.datn_qlnt_manager.repository.PaymentHistoryRepository;
import com.example.datn_qlnt_manager.repository.PaymentReceiptRepository;

import com.example.datn_qlnt_manager.repository.TenantRepository;

import com.example.datn_qlnt_manager.service.EmailService;

import com.example.datn_qlnt_manager.service.PaymentReceiptService;
import com.example.datn_qlnt_manager.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentReceiptServiceImpl implements PaymentReceiptService {

    InvoiceRepository invoiceRepository;
    PaymentReceiptRepository paymentReceiptRepository;
    TenantRepository tenantRepository;
    PaymentHistoryRepository paymentHistoryRepository;
    CodeGeneratorService codeGeneratorService;
    UserService userService;
    EmailService emailService;
    PaymentReceiptMapper paymentReceiptMapper;
    VnpayConfig vnpayConfig;


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
                .note(request.getNote() != null && !request.getNote().isBlank() ? request.getNote() : "Không có ghi " +
                        "chú!")
                .build();

        receipt.setCreatedAt(Instant.now());
        receipt.setUpdatedAt(Instant.now());
        return paymentReceiptMapper.toResponse(paymentReceiptRepository.save(receipt));
    }

    @Override
    public PaginatedResponse<PaymentReceiptResponse> filterPaymentReceiptsByUserId(
            PaymentReceiptFilter filter, int page, int size) {
        User user = userService.getCurrentUser();

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        Page<PaymentReceipt> paymentPage = paymentReceiptRepository.filterPaymentReceipts(
                user.getId(),
                filter.getQuery(),
                filter.getPaymentStatus(),
                filter.getPaymentMethod(),
                filter.getFromAmount(),
                filter.getToAmount(),
                filter.getFromDate(),
                filter.getToDate(),
                pageable
        );

        return buildPaginatedPaymentReceiptResponse(paymentPage, page, size);
    }

    @Override
    public PaginatedResponse<PaymentReceiptResponse> filterPaymentReceiptsByTenantId(PaymentReceiptFilter filter,
                                                                                     int page, int size) {
        User user = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Tenant tenant = tenantRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        Page<PaymentReceipt> paymentPage = paymentReceiptRepository.findAllByTenantId(
                tenant.getId(),
                filter.getQuery(),
                filter.getPaymentStatus(),
                filter.getPaymentMethod(),
                filter.getFromAmount(),
                filter.getToAmount(),
                filter.getFromDate(),
                filter.getToDate(),
                pageable
        );

        return buildPaginatedPaymentReceiptResponse(paymentPage, page, size);
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

            receipt.setCreatedAt(Instant.now());
            receipt.setUpdatedAt(Instant.now());

            paymentReceiptRepository.save(receipt);
            created++;

            //Cập nhật trạng thái hóa đơn
            invoice.setInvoiceStatus(InvoiceStatus.CHO_THANH_TOAN);
            invoice.setUpdatedAt(Instant.now());
            invoiceRepository.save(invoice);

            //Gửi email tới tất cả khách thuê có tài khoản
            invoice.getContract().getContractTenants().forEach(ct -> {
                User user = ct.getTenant().getUser();
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
            case TIEN_MAT, CHUYEN_KHOAN -> {
                receipt.setPaymentMethod(request.getPaymentMethod());
                receipt.setPaymentStatus(PaymentStatus.CHO_XAC_NHAN);
                receipt.setUpdatedAt(Instant.now());
                paymentReceiptRepository.save(receipt);
                emailService.notifyOwnerForCashReceipt(receipt);
            }

            case VNPAY -> {
                Invoice invoice = invoiceRepository.findById(receipt.getInvoice().getId())
                                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));
                receipt.setPaymentMethod(request.getPaymentMethod());
                receipt.setPaymentStatus(PaymentStatus.DA_THANH_TOAN);
                receipt.setUpdatedAt(Instant.now());
                receipt.setPaymentDate(LocalDateTime.now());
                invoice.setInvoiceStatus(InvoiceStatus.DA_THANH_TOAN);
                paymentReceiptRepository.save(receipt);
                invoiceRepository.save(invoice);
                emailService.notifyOwnerForCashReceipt(receipt);
            }

            case ZALOPAY, MOMO -> throw new AppException(ErrorCode.NOT_SUPPORTED_YET);

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

        logPaymentHistory(receipt, PaymentAction.TU_CHOI, "Từ chối thanh toán: " + request.getReason());

        emailService.notifyOwnerRejectedReceipt(receipt);

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

        if (receipt.getPaymentMethod() != PaymentMethod.TIEN_MAT && receipt.getPaymentMethod() != PaymentMethod.CHUYEN_KHOAN) {
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

        logPaymentHistory(receipt, PaymentAction.DA_THANH_TOAN, "Thanh toán thành công cho hóa đơn " + invoice.getInvoiceCode());

        emailService.notifyTenantPaymentConfirmed(receipt);
    }

    @Override
    public PaymentReceiptResponse findPaymentReceiptByInvoiceId(String invoiceId) {
        return paymentReceiptMapper.toResponse(paymentReceiptRepository.findByInvoiceId(invoiceId));
    }

    @Override
    public String createPaymentUrl(PaymentCreationURL paymentCreationURL, HttpServletRequest request) {
        String version = "2.1.0";
        String command = "pay";
        String orderType = "other";
        Long amount = paymentCreationURL.getAmount() * 100;
        String bankCode = paymentCreationURL.getBankCode();

        StringBuilder transactionReference = new StringBuilder();
        transactionReference.append(paymentCreationURL.getTransactionReferenceCode())
                .append("_").append(VnpayConfig.getRandomNumber(8));
        String clientIdAddress = VnpayConfig.getIpAddress(request);

        String terminalCode = vnpayConfig.vnp_TmnCode;

        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", version);
        params.put("vnp_Command", command);
        params.put("vnp_TmnCode", terminalCode);
        params.put("vnp_Amount", String.valueOf(amount));
        params.put("vnp_CurrCode", "VND");

        if (bankCode != null && !bankCode.isEmpty()) {
            params.put("vnp_BankCode", bankCode);
        }
        params.put("vnp_TxnRef", transactionReference.toString());
        params.put("vnp_OrderInfo", "Thanh toan don hang:" + transactionReference);
        params.put("vnp_OrderType", orderType);

        String locate = paymentCreationURL.getLanguage();
        if (locate != null && !locate.isEmpty()) {
            params.put("vnp_Locale", locate);
        } else {
            params.put("vnp_Locale", "vn");
        }
        params.put("vnp_ReturnUrl", vnpayConfig.vnp_ReturnUrl);
        params.put("vnp_IpAddr", clientIdAddress);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String createDate = formatter.format(cld.getTime());
        params.put("vnp_CreateDate", createDate);

        cld.add(Calendar.MINUTE, 15);
        String expireDate = formatter.format(cld.getTime());
        params.put("vnp_ExpireDate", expireDate);

        List<String> sortedFieldNames = new ArrayList<>(params.keySet());
        Collections.sort(sortedFieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder queryData = new StringBuilder();

        for (Iterator<String> iterator = sortedFieldNames.iterator(); iterator.hasNext(); ) {
            String fieldName = iterator.next();
            String fieldValue = params.get(fieldName);

            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append("=").append(URLEncoder.encode(fieldValue,
                        StandardCharsets.US_ASCII));
                queryData.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII)).append("=").append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (iterator.hasNext()) {
                    hashData.append("&");
                    queryData.append("&");
                }
            }
        }

        String secureHash = VnpayConfig.hmacSHA512(vnpayConfig.secretKey, hashData.toString());
        queryData.append("&vnp_SecureHash=").append(secureHash);
        return vnpayConfig.vnp_PayUrl + "?" + queryData;
    }

    private PaginatedResponse<PaymentReceiptResponse> buildPaginatedPaymentReceiptResponse(
            Page<PaymentReceipt> paymentPage, int page, int size) {

        List<PaymentReceiptResponse> responses = paymentPage
                .getContent()
                .stream()
                .map(paymentReceiptMapper::toResponse)
                .toList();

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

    private void logPaymentHistory(PaymentReceipt receipt, PaymentAction paymentAction, String note) {
        PaymentHistory history = PaymentHistory.builder()
                .paymentReceipt(receipt)
                .time(LocalDateTime.now())
                .paymentAction(paymentAction)
                .note(note)
                .build();

        paymentHistoryRepository.save(history);
    }

}
