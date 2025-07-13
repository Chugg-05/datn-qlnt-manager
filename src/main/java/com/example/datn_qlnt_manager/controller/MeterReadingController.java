package com.example.datn_qlnt_manager.controller;


import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.MeterReadingFilter;
import com.example.datn_qlnt_manager.dto.request.meterReading.MeterReadingCreationRequest;
import com.example.datn_qlnt_manager.dto.request.meterReading.MeterReadingUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.meterReading.MeterReadingResponse;
import com.example.datn_qlnt_manager.service.MeterReadingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meter-readings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "MeterReading", description = "API MeterReading")
public class MeterReadingController {

    MeterReadingService meterReadingService;

    @Operation(summary = "Hiển thị & lọc chỉ số công tơ")
    @GetMapping
    public ApiResponse<List<MeterReadingResponse>> filterMeterReadings(
            @ModelAttribute MeterReadingFilter meterReadingFilter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        PaginatedResponse<MeterReadingResponse> result = meterReadingService
                .getPageAndSearchAndFilterMeterReadingByUserId(meterReadingFilter, page, size);

        return ApiResponse.<List<MeterReadingResponse>>builder()
                .data(result.getData())
                .meta(result.getMeta())
                .message("Filter meter readings success")
                .build();
    }

    @Operation(summary = "Thêm chỉ số công tơ")
    @PostMapping
    public ApiResponse<MeterReadingResponse> create(@Valid @RequestBody MeterReadingCreationRequest request) {
        return ApiResponse.<MeterReadingResponse>builder()
                .data(meterReadingService.createMeterReading(request))
                .message("Add meter reading success")
                .build();
    }

    @Operation(summary = "Sửa chỉ số công tơ")
    @PutMapping("/{meterReadingId}")
    public ApiResponse<MeterReadingResponse> update(
            @PathVariable String meterReadingId, @RequestBody @Valid MeterReadingUpdateRequest request) {
        return ApiResponse.<MeterReadingResponse>builder()
                .data(meterReadingService.updateMeterReading(meterReadingId, request))
                .message("Update meter reading success")
                .build();
    }

    @Operation(summary = "Xóa chỉ số công tơ")
    @DeleteMapping("/{meterReadingId}")
    public ApiResponse<Void> delete(@PathVariable String meterReadingId) {
        meterReadingService.deleteMeterReading(meterReadingId);
        return ApiResponse.<Void>builder()
                .message("Delete meter reading success")
                .build();
    }

    @Operation(summary = "Xem danh sách chỉ số từng công tơ")
    @GetMapping("/{meterReadingId}")
    public ApiResponse<MeterReadingResponse> getById(@PathVariable String meterReadingId) {
        return ApiResponse.<MeterReadingResponse>builder()
                .data(meterReadingService.getMeterReadingById(meterReadingId))
                .message("Get meter reading success")
                .build();
    }


}
