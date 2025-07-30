package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.PaymentReceiptFilter;
import com.example.datn_qlnt_manager.dto.request.paymentReceipt.PaymentReceiptCreationRequest;
import com.example.datn_qlnt_manager.dto.request.paymentReceipt.PaymentReceiptStatusUpdateRequest;
import com.example.datn_qlnt_manager.dto.request.paymentReceipt.PaymentReceiptUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.paymentReceipt.PaymentReceiptResponse;

public interface PaymentReceiptService {
    PaymentReceiptResponse createPaymentReceipt(PaymentReceiptCreationRequest request);

    PaginatedResponse<PaymentReceiptResponse> filterPaymentReceiptsByUserId( PaymentReceiptFilter filter, int page, int size);

    PaginatedResponse<PaymentReceiptResponse> filterPaymentReceiptsByTenantId(PaymentReceiptFilter filter, int page, int size);

    PaymentReceiptResponse updatePaymentReceipt(String paymentReceiptId, PaymentReceiptUpdateRequest request);

    void deletePaymentReceipt(String paymentReceiptId);

    PaymentReceiptResponse updatePaymentReceiptStatus(String paymentReceiptId, PaymentReceiptStatusUpdateRequest request);

    PaymentReceiptResponse confirmPaymentReceipt(String paymentReceiptId);


}
