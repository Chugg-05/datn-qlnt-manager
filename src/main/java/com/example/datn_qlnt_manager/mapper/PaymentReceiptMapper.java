package com.example.datn_qlnt_manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.datn_qlnt_manager.dto.response.paymentReceipt.PaymentReceiptResponse;
import com.example.datn_qlnt_manager.entity.PaymentReceipt;

@Mapper(componentModel = "spring")
public interface PaymentReceiptMapper {

    @Mapping(target = "invoiceId", source = "invoice.id")
    @Mapping(target = "invoiceCode", source = "invoice.invoiceCode")
    PaymentReceiptResponse toResponse(PaymentReceipt paymentReceipt);
}
