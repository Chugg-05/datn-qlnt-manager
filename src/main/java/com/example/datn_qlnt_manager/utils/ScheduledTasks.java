package com.example.datn_qlnt_manager.utils;

import com.example.datn_qlnt_manager.service.AutoTaskService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScheduledTasks {
    AutoTaskService autoTaskService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void executeScheduledTask() {
        autoTaskService.contractIsAboutToExpire();
        autoTaskService.expiredContract();
        autoTaskService.roomOutOfContract();
        autoTaskService.guestHasCheckedOut();
        autoTaskService.expiredInvoice();
        autoTaskService.noDepositRefund();
        autoTaskService.deleteCancelledEntities();
    }
}
