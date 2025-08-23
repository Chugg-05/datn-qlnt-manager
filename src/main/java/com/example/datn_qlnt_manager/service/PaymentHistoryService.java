package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.PaymentHistoryFilter;
import com.example.datn_qlnt_manager.dto.response.paymenthistory.PaymentHistoryResponse;

public interface PaymentHistoryService {
    PaginatedResponse<PaymentHistoryResponse> filterPaymentHistoriesByUserId(
            PaymentHistoryFilter filter, int page, int size);

    PaginatedResponse<PaymentHistoryResponse> filterMyPaymentHistories(
            PaymentHistoryFilter filter, int page, int size);
}
