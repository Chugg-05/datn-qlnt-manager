package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.DepositFilter;
import com.example.datn_qlnt_manager.dto.projection.DepositDetailView;
import com.example.datn_qlnt_manager.dto.response.deposit.ConfirmDepositResponse;
import com.example.datn_qlnt_manager.entity.Contract;
import org.springframework.transaction.annotation.Transactional;

public interface DepositService {
    PaginatedResponse<DepositDetailView> getDepositsByUserId(
            DepositFilter filter,
            int page,
            int size
    );

    PaginatedResponse<DepositDetailView> getDepositsForTenant(
            DepositFilter filter,
            int page,
            int size
    );

    @Transactional
    void createDepositForContract(Contract contract);

    @Transactional
    ConfirmDepositResponse confirmDepositRefund(String depositId);

    @Transactional
    ConfirmDepositResponse confirmReceipt(String depositId);

    @Transactional
    ConfirmDepositResponse confirmedNotReceived(String depositId);

    void deleteDepositById(String depositId);
}
