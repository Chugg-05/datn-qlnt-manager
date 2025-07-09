package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.InvoiceFilter;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceDetailsResponse;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceResponse;

public interface InvoiceService {
    PaginatedResponse<InvoiceResponse> getPageAndSearchAndFilterByUserId(
            InvoiceFilter filter,
            int page,
            int size
    );

    PaginatedResponse<InvoiceResponse> getInvoiceWithStatusCancelByUserId(
            InvoiceFilter filter,
            int page,
            int size
    );

    InvoiceDetailsResponse getInvoiceDetails(String invoiceId);
}
