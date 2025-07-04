package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.MeterFilter;
import com.example.datn_qlnt_manager.dto.request.meter.MeterCreationRequest;
import com.example.datn_qlnt_manager.dto.request.meter.MeterUpdateRequest;
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

    @Operation(summary = "Phân trang, tìm kiếm, lọc congto")
    @GetMapping
    public ApiResponse<List<MeterResponse>> getPageAndSearchAndFilterMeter(
            @ModelAttribute MeterFilter meterFilter,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "15") Integer size
    ) {
        PaginatedResponse<MeterResponse> result = meterService.getPageAndSearchAndFilterMeterByUserId(
                meterFilter,
                page,
                size
        );

        return ApiResponse.<List<MeterResponse>>builder()
                .message("Filter users successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }
    @PostMapping("/add")
    public ApiResponse<MeterResponse> createMeter(@RequestBody @Valid MeterCreationRequest request) {
        return ApiResponse.<MeterResponse>builder()
                .data(meterService.createMeter(request))
                .message("Add meter success")
                .code(201)
                .build();
    }

    @PutMapping("/update/{id}")
    public ApiResponse<MeterResponse> updateMeter(
            @PathVariable("id") String meterId,
            @RequestBody @Valid MeterUpdateRequest request) {
        return ApiResponse.<MeterResponse>builder()
                .data(meterService.updateMeter(meterId, request))
                .message("Update meter success")
                .code(200)
                .build();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteRoom(@PathVariable("id") String meterId) {
        return ApiResponse.<Void>builder()
                .data(meterService.deleteMeter(meterId))
                .message("Delete meter success")
                .code(200)
                .build();
    }
}
