package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.InvoiceFilter;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceResponse;

public interface InvoiceService {
    PaginatedResponse<InvoiceResponse> getPageAndSearchAndFilter(
            InvoiceFilter filter,
            int page,
            int size
    );
}
