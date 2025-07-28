package com.example.datn_qlnt_manager.service.strategy;

import java.util.Random;

import com.example.datn_qlnt_manager.entity.Invoice;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReceiptCodeStrategy {
    public String generateReceiptCode(Invoice invoice) {
        String contractCode = invoice.getContract().getContractCode();
        String randomNumber = String.format("%06d", new Random().nextInt(1_000_000));
        return "MPTT-" + contractCode + "-" + randomNumber;
    }
}
