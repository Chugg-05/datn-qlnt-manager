package com.example.datn_qlnt_manager.service;

public interface AutoTaskService {
    void contractIsAboutToExpire();

    void expiredContract();

    void roomOutOfContract();

    void expiredInvoice();

    void noDepositRefund();

    void guestHasCheckedOut();

    void deleteCancelledTenants();
}
