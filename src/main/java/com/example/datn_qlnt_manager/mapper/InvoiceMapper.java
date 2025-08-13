package com.example.datn_qlnt_manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.datn_qlnt_manager.common.InvoiceItemType;
import com.example.datn_qlnt_manager.common.ServiceCalculation;
import com.example.datn_qlnt_manager.common.ServiceCategory;
import com.example.datn_qlnt_manager.dto.request.invoice.InvoiceUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceItemResponse;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceResponse;
import com.example.datn_qlnt_manager.entity.Invoice;
import com.example.datn_qlnt_manager.entity.InvoiceDetail;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(target = "roomId", source = "contract.room.id")
    @Mapping(source = "id", target = "id")
    @Mapping(target = "paymentReceiptId", expression = "java(getFirstReceiptId(invoice))")
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
//    @Mapping(target = "tenantName", expression = "java(getRepresentativeName(invoice))")
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

//    default String getRepresentativeName(Invoice invoice) {
//        if (invoice.getContract() == null || invoice.getContract().getContractTenants() == null) return null;
//        return invoice.getContract().getContractTenants().stream()
//                .filter(t -> Boolean.TRUE.equals(t.getTenant().getHasAccount()))
//                .map(ContractTen::getFullName)
//                .findFirst()
//                .orElse(null);
//    }

    default InvoiceItemResponse toItemResponse(InvoiceDetail detail) {
        ServiceCategory category = null;
        ServiceCalculation calculation = null;
        String serviceRoomId = null;
        String unit = null;

        if (detail.getInvoiceItemType() == InvoiceItemType.TIEN_PHONG) {
            category = ServiceCategory.TIEN_PHONG;
            calculation = ServiceCalculation.TINH_THEO_PHONG;
            unit = "phòng";
        } else if (detail.getServiceRoom() != null && detail.getServiceRoom().getService() != null) {
            var service = detail.getServiceRoom().getService();
            serviceRoomId = detail.getServiceRoom().getId();
            category = service.getServiceCategory();
            calculation = service.getServiceCalculation();
            unit = service.getUnit();
        } else if (detail.getInvoiceItemType() == InvoiceItemType.DEN_BU) {
            category = ServiceCategory.DEN_BU;
        }

        return InvoiceItemResponse.builder()
                .id(detail.getId())
                .serviceName(detail.getServiceName())
                .serviceRoomId(serviceRoomId)
                .serviceCategory(category)
                .serviceCalculation(calculation)
                .oldIndex(detail.getOldIndex())
                .newIndex(detail.getNewIndex())
                .quantity(detail.getQuantity())
                .unitPrice(detail.getUnitPrice())
                .unit(unit)
                .amount(detail.getAmount())
                .description(detail.getDescription())
                .build();
    }

    default String getFirstReceiptId(Invoice invoice) {
        if (invoice.getPaymentReceipts() == null || invoice.getPaymentReceipts().isEmpty()) return null;
        return invoice.getPaymentReceipts().getFirst().getId();
    }

}
