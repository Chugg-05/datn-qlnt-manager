package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.common.MeterType;
import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.MeterReadingFilter;
import com.example.datn_qlnt_manager.dto.request.meterReading.MeterReadingCreationRequest;
import com.example.datn_qlnt_manager.dto.request.meterReading.MeterReadingUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.meterReading.MeterReadingResponse;
import com.example.datn_qlnt_manager.service.MeterReadingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meter-reading")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "MeterReading", description = "API MeterReading")
public class MeterReadingController {

    MeterReadingService meterReadingService;

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
                .code(200)
                .build();
    }

    @PostMapping("/add")
    public ApiResponse<MeterReadingResponse> create(@RequestBody @Valid MeterReadingCreationRequest request) {
        return ApiResponse.<MeterReadingResponse>builder()
                .data(meterReadingService.createMeterReading(request))
                .message("Add meter reading success")
                .code(201)
                .build();
    }

    @PutMapping("/update/{id}")
    public ApiResponse<MeterReadingResponse> update(
            @PathVariable String id, @RequestBody @Valid MeterReadingUpdateRequest request) {
        return ApiResponse.<MeterReadingResponse>builder()
                .data(meterReadingService.updateMeterReading(id, request))
                .message("Update meter reading success")
                .code(200)
                .build();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        meterReadingService.deleteMeterReading(id);
        return ApiResponse.<Void>builder()
                .message("Delete meter reading success")
                .code(200)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<MeterReadingResponse> getById(@PathVariable String id) {
        return ApiResponse.<MeterReadingResponse>builder()
                .data(meterReadingService.getMeterReadingById(id))
                .message("Get meter reading success")
                .code(200)
                .build();
    }


}
