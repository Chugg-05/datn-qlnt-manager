package com.example.datn_qlnt_manager.mapper;

import com.example.datn_qlnt_manager.common.ServiceCategory;
import com.example.datn_qlnt_manager.dto.request.invoice.InvoiceUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceItemResponse;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceResponse;
import com.example.datn_qlnt_manager.entity.Invoice;
import com.example.datn_qlnt_manager.entity.InvoiceDetail;
import com.example.datn_qlnt_manager.entity.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "invoiceCode", target = "invoiceCode")
    @Mapping(source = "contract.room.floor.building.buildingName", target = "buildingName")
    @Mapping(source = "contract.room.roomCode", target = "roomCode")
    @Mapping(source = "month", target = "month")
    @Mapping(source = "year", target = "year")
    @Mapping(source = "totalAmount", target = "totalAmount")
    @Mapping(source = "paymentDueDate", target = "paymentDueDate")
    @Mapping(source = "invoiceStatus", target = "invoiceStatus")
    @Mapping(source = "invoiceType", target = "invoiceType")
    @Mapping(source = "note", target = "note")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(target = "tenantName", expression = "java(getRepresentativeName(invoice))")
    InvoiceResponse toInvoiceResponse(Invoice invoice);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contract", ignore = true)
    @Mapping(target = "invoiceCode", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "month", ignore = true)
    @Mapping(target = "year", ignore = true)
    @Mapping(target = "invoiceStatus", ignore = true)
    @Mapping(target = "invoiceType", ignore = true)
    void updateInvoice(InvoiceUpdateRequest request, @MappingTarget Invoice invoice);

    default String getRepresentativeName(Invoice invoice) {
        if (invoice.getContract() == null || invoice.getContract().getTenants() == null) return null;
        return invoice.getContract().getTenants().stream()
                .filter(t -> Boolean.TRUE.equals(t.getIsRepresentative()))
                .map(Tenant::getFullName)
                .findFirst()
                .orElse(null);
    }

    default InvoiceItemResponse toItemResponse(InvoiceDetail detail) {
        ServiceCategory serviceType = null;
        if (detail.getServiceRoom() != null && detail.getServiceRoom().getService() != null) {
//            serviceType = detail.getServiceRoom().getService().getType();
        }

        return InvoiceItemResponse.builder()
                .id(detail.getId())
                .serviceName(detail.getServiceName())
                .serviceType(serviceType)
                .oldIndex(detail.getOldIndex())
                .newIndex(detail.getNewIndex())
                .quantity(detail.getQuantity())
                .unitPrice(detail.getUnitPrice())
                .amount(detail.getAmount())
                .build();
    }

}
