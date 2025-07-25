package com.example.datn_qlnt_manager.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.example.datn_qlnt_manager.dto.projection.InvoiceDetailView;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceDetailsResponse;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceItemResponse;

@Mapper(componentModel = "spring")
public interface InvoiceDetailsMapper {
    InvoiceDetailsResponse toResponse(InvoiceDetailView view, List<InvoiceItemResponse> items);
}
