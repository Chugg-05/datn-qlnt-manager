package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.MeterFilter;
import com.example.datn_qlnt_manager.dto.request.meter.MeterCreationRequest;
import com.example.datn_qlnt_manager.dto.request.meter.MeterUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.meter.MeterReadingMonthlyStatsResponse;
import com.example.datn_qlnt_manager.dto.response.meter.MeterResponse;
import com.example.datn_qlnt_manager.service.MeterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;



import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@RestController
@RequestMapping("/meters")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Meter", description = "API Meter")
public class MeterController {

    MeterService meterService;

    @Operation(summary = "Phân trang, lọc công tơ theo tòa, phòng, loại công tơ, mã/tên")
    @GetMapping
    public ApiResponse<PaginatedResponse<MeterResponse>> getMyMeters(
            @Valid @ModelAttribute MeterFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return ApiResponse.<PaginatedResponse<MeterResponse>>builder()
                .data(meterService.getPageAndSearchAndFilterMeterByUserId(filter, page, size))
                .message("Get my meter list successfully")
                .build();
    }

    @Operation(summary = "Thêm công tơ")
    @PostMapping
    public ApiResponse<MeterResponse> createMeter(@RequestBody @Valid MeterCreationRequest request) {
        return ApiResponse.<MeterResponse>builder()
                .data(meterService.createMeter(request))
                .message("Add meter success")
                .build();
    }

    @Operation(summary = "Sửa công tơ")
    @PutMapping("/{meterId}")
    public ApiResponse<MeterResponse> updateMeter(
            @PathVariable("meterId") String meterId,
            @RequestBody @Valid MeterUpdateRequest request) {
        return ApiResponse.<MeterResponse>builder()
                .data(meterService.updateMeter(meterId, request))
                .message("Update meter success")
                .build();
    }

    @Operation(summary = "Xóa công tơ")
    @DeleteMapping("/{meterId}")
    public ApiResponse<String> deleteRoom(@PathVariable("meterId") String meterId) {
        meterService.deleteMeter(meterId);
        return ApiResponse.<String>builder()
                .message("Delete meter success")
                .build();
    }

    @GetMapping("/monthly-stats/{meterCode}")
    public ApiResponse<List<MeterReadingMonthlyStatsResponse>> getMonthlyStats(@PathVariable String meterCode) {
        return ApiResponse.<List<MeterReadingMonthlyStatsResponse>>builder()
                .message("Thống kê chỉ số từng tháng thành công")
                .data(meterService.getMonthlyStats(meterCode))
                .build();
    }


}
