package com.example.datn_qlnt_manager.mapper;

import com.example.datn_qlnt_manager.dto.projection.InvoiceDetailView;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceDetailsResponse;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceItemResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceDetailsMapper {
    InvoiceDetailsResponse toResponse(InvoiceDetailView view, List<InvoiceItemResponse> items);
}
