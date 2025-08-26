package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.DepositFilter;
import com.example.datn_qlnt_manager.dto.projection.DepositDetailView;
import com.example.datn_qlnt_manager.dto.response.deposit.ConfirmDepositResponse;
import com.example.datn_qlnt_manager.service.DepositService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/deposits")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Deposit", description = "API Deposit")
public class DepositController {

    DepositService depositService;

    @Operation(summary = "Danh sach cọc theo user ID")
    @GetMapping
    public ApiResponse<List<DepositDetailView>> getDeposits(
            @ModelAttribute DepositFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {

        PaginatedResponse<DepositDetailView> result =
                depositService.getDepositsByUserId(filter, page, size);

        return ApiResponse.<List<DepositDetailView>>builder()
                .message("Get deposits successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Danh sach cọc theo user ID cho khach thue")
    @GetMapping("/tenant")
    public ApiResponse<List<DepositDetailView>> getDepForTenant(
            @ModelAttribute DepositFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {

        PaginatedResponse<DepositDetailView> result =
                depositService.getDepositsForTenant(filter, page, size);

        return ApiResponse.<List<DepositDetailView>>builder()
                .message("Get deposits successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Gửi thông báo xác nhận đã trả cọc tới khách hàng")
    @PostMapping("/confirm-refund/{depositId}")
    public ApiResponse<ConfirmDepositResponse> confirmDepositRefund(@PathVariable("depositId") String depositId) {

        ConfirmDepositResponse response = depositService.confirmDepositRefund(depositId);

        return ApiResponse.<ConfirmDepositResponse>builder()
                .message("Deposit refund confirmation sent successfully")
                .data(response)
                .build();
    }

    @Operation(summary = "Gửi thông báo xác nhận đã nhận đủ cọc tới chủ nhà")
    @PostMapping("/confirm-receipt/{depositId}")
    public ApiResponse<ConfirmDepositResponse> confirmReceipt(@PathVariable("depositId") String depositId) {

        ConfirmDepositResponse response = depositService.confirmReceipt(depositId);

        return ApiResponse.<ConfirmDepositResponse>builder()
                .message("Deposit receipt confirmed successfully")
                .data(response)
                .build();
    }

    @Operation(summary = "Gửi thông báo chưa nhận đủ cọc tới chủ nhà")
    @PostMapping("/not-received/{depositId}")
    public ApiResponse<ConfirmDepositResponse> confirmedNotReceived(@PathVariable("depositId") String depositId) {

        ConfirmDepositResponse response = depositService.confirmedNotReceived(depositId);

        return ApiResponse.<ConfirmDepositResponse>builder()
                .message("Deposit confirmation sent successfully")
                .data(response)
                .build();
    }

    @Operation(summary = "Delete deposit by ID")
    @DeleteMapping("/{depositId}")
    public ApiResponse<String> deleteDeposit(@PathVariable("depositId") String depositId) {
        depositService.deleteDepositById(depositId);

        return ApiResponse.<String>builder()
                .message("Deposit deleted successfully")
                .data("Deposit with ID " + depositId + " has been deleted.")
                .build();

    }
}
