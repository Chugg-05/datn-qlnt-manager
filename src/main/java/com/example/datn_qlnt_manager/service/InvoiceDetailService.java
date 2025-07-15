package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.request.invoiceDetail.InvoiceDetailCreationRequest;
import com.example.datn_qlnt_manager.dto.request.invoiceDetail.InvoiceDetailUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceItemResponse;

public interface InvoiceDetailService {
    InvoiceItemResponse createInvoiceDetail(InvoiceDetailCreationRequest request);

    InvoiceItemResponse updateInvoiceDetail(String detailId, InvoiceDetailUpdateRequest request);

    void deleteInvoiceDetail(String detailId);
}
