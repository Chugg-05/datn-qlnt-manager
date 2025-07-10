package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.response.feedbackProcessHistory.FeedbackProcessHistoryResponse;
import com.example.datn_qlnt_manager.service.FeedbackProcessHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/feedback-logs")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class FeedbackProcessHistoryController {

    FeedbackProcessHistoryService feedbackProcessHistoryService;

    @Operation(summary = "Lịch sử xử lý phản hồi của người dùng hiện tại")
    @GetMapping
    public ApiResponse<PaginatedResponse<FeedbackProcessHistoryResponse>> getAllByUserId(
            @RequestParam(required = false) String feedbackId,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return ApiResponse.<PaginatedResponse<FeedbackProcessHistoryResponse>>builder()
                .data(feedbackProcessHistoryService.getAllByUserId(feedbackId, query, page, size))
                .message("Response processing history loaded successfully")
                .build();
    }
}
