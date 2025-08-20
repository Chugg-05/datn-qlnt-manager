package com.example.datn_qlnt_manager.service;

import java.util.List;

import com.example.datn_qlnt_manager.common.InvoiceType;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceItemResponse;
import com.example.datn_qlnt_manager.entity.Contract;

public interface InvoiceChargeCalculatorService {

    List<InvoiceItemResponse> generateInvoiceItems(
            Contract contract, int month, int year, InvoiceType invoiceType, List<InvoiceItemResponse> additionalItems);
}
