package com.example.datn_qlnt_manager.service;

public interface AutoTaskService {
    void updateContractStatus();

    void finalizeExpiredContracts();

    void updateExpiredInvoices();
}
