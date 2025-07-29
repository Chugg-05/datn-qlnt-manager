package com.example.datn_qlnt_manager.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.InvoiceFilter;
import com.example.datn_qlnt_manager.dto.request.invoice.InvoiceBuildingCreationRequest;
import com.example.datn_qlnt_manager.dto.request.invoice.InvoiceCreationRequest;
import com.example.datn_qlnt_manager.dto.request.invoice.InvoiceFloorCreationRequest;
import com.example.datn_qlnt_manager.dto.request.invoice.InvoiceUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceDetailsResponse;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceResponse;
import com.example.datn_qlnt_manager.dto.statistics.InvoiceStatistics;

public interface InvoiceService {
    PaginatedResponse<InvoiceResponse> getPageAndSearchAndFilterByUserId(InvoiceFilter filter, int page, int size);

    PaginatedResponse<InvoiceResponse> getInvoiceWithStatusCancelByUserId(InvoiceFilter filter, int page, int size);

    InvoiceDetailsResponse getInvoiceDetails(String invoiceId);

    InvoiceResponse createInvoiceForContract(InvoiceCreationRequest request);

    List<InvoiceResponse> createInvoicesForBuilding(InvoiceBuildingCreationRequest request);

    List<InvoiceResponse> createInvoicesForFloor(InvoiceFloorCreationRequest request);

    InvoiceResponse createEndOfMonthInvoice(InvoiceCreationRequest request);

    @Transactional
    InvoiceResponse updateInvoice(String invoiceId, InvoiceUpdateRequest request);

    InvoiceStatistics getInvoiceStatistics();

    @Transactional
    void toggleInvoiceStatus(String invoiceId);

    @Transactional
    void softDeleteInvoice(String invoiceId);

    void deleteInvoice(String invoiceId);

    List<InvoiceResponse> getAllInvoicesByUserId();

    PaginatedResponse<InvoiceResponse> getInvoicesForTenant(
            InvoiceFilter filter, int page, int size);
}
