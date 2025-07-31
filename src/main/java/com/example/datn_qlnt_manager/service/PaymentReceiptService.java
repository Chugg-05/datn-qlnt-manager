package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.PaymentReceiptFilter;
import com.example.datn_qlnt_manager.dto.request.paymentReceipt.*;
import com.example.datn_qlnt_manager.dto.response.paymentReceipt.PaymentBatchResponse;
import com.example.datn_qlnt_manager.dto.response.paymentReceipt.PaymentMethodResponse;
import com.example.datn_qlnt_manager.dto.response.paymentReceipt.PaymentReceiptResponse;
import com.example.datn_qlnt_manager.dto.response.paymentReceipt.RejectPaymentResponse;
import org.springframework.transaction.annotation.Transactional;

public interface PaymentReceiptService {
    PaymentReceiptResponse createPaymentReceipt(PaymentReceiptCreationRequest request);

    PaginatedResponse<PaymentReceiptResponse> filterPaymentReceiptsByUserId( PaymentReceiptFilter filter, int page, int size);


    PaginatedResponse<PaymentReceiptResponse> filterPaymentReceiptsByTenantId(PaymentReceiptFilter filter, int page, int size);

    void deletePaymentReceipt(String paymentReceiptId);

    @Transactional
    PaymentBatchResponse generateMonthlyPaymentRequests();

    @Transactional
    PaymentMethodResponse confirmPaymentMethod(String receiptId, PaymentMethodRequest request);

    @Transactional
    RejectPaymentResponse rejectPaymentReceipt(String receiptId, RejectPaymentRequest request);

    @Transactional
    void confirmCashPayment(String receiptId);

    PaymentReceiptResponse findPaymentReceiptByInvoiceId(String invoiceId);
}
