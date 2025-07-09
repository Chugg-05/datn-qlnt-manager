package com.example.datn_qlnt_manager.mapper;

import com.example.datn_qlnt_manager.dto.projection.InvoiceDetailView;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceDetailsResponse;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceItemResponse;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceResponse;
import com.example.datn_qlnt_manager.entity.Invoice;
import com.example.datn_qlnt_manager.entity.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(source = "invoiceCode", target = "invoiceCode")
    @Mapping(source = "contract.room.floor.building.buildingName", target = "buildingName")
    @Mapping(source = "contract.room.roomCode", target = "roomCode")
    @Mapping(source = "month", target = "month")
    @Mapping(source = "year", target = "year")
    @Mapping(source = "grandTotal", target = "totalAmount")
    @Mapping(source = "paymentDueDate", target = "paymentDueDate")
    @Mapping(source = "invoiceStatus", target = "invoiceStatus")
    @Mapping(source = "note", target = "note")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(target = "tenantName", expression = "java(getRepresentativeName(invoice))")
    InvoiceResponse toInvoiceResponse(Invoice invoice);

    default String getRepresentativeName(Invoice invoice) {
        if (invoice.getContract() == null || invoice.getContract().getTenants() == null) return null;
        return invoice.getContract().getTenants().stream()
                .filter(t -> Boolean.TRUE.equals(t.getIsRepresentative()))
                .map(Tenant::getFullName)
                .findFirst()
                .orElse(null);
    }

    InvoiceDetailsResponse toResponse(InvoiceDetailView view, List<InvoiceItemResponse> items);

}
