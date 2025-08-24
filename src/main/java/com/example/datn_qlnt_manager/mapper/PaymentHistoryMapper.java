package com.example.datn_qlnt_manager.mapper;

import com.example.datn_qlnt_manager.dto.response.paymenthistory.PaymentHistoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import com.example.datn_qlnt_manager.entity.PaymentHistory;

@Mapper(componentModel = "spring")
public interface PaymentHistoryMapper {

    @Mapping(target = "receiptId", source = "paymentReceipt.id")
    @Mapping(target = "receiptCode", source = "paymentReceipt.receiptCode")
    @Mapping(target = "invoiceId", source = "paymentReceipt.invoice.id")
    @Mapping(target = "invoiceCode", source = "paymentReceipt.invoice.invoiceCode")
    @Mapping(target = "roomCode", source = "paymentReceipt.invoice.contract.room.roomCode")
    @Mapping(target = "amount", source = "paymentReceipt.amount")
    @Mapping(target = "paymentMethod", source = "paymentReceipt.paymentMethod")
    PaymentHistoryResponse toResponse(PaymentHistory entity);
}
