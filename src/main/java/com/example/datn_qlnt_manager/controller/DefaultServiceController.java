package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.DefaultServiceFilter;
import com.example.datn_qlnt_manager.dto.request.DefaultService.DefaultServiceCreationRequest;
import com.example.datn_qlnt_manager.dto.request.DefaultService.DefaultServiceUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.defaultService.DefaultServiceResponse;
import com.example.datn_qlnt_manager.service.DefaultServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/default-services")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Default Service", description = "API Default Service")
public class DefaultServiceController {
    DefaultServiceService defaultServiceService;

    @Operation(summary = "Phân trang, lọc dịch vụ mặc định")
    @GetMapping
    public ApiResponse<List<DefaultServiceResponse>> filterDefaultServices (
            @ModelAttribute DefaultServiceFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size
            ){
        PaginatedResponse<DefaultServiceResponse> result = defaultServiceService.filterDefaultServices(filter, page, size);

        return ApiResponse.<List<DefaultServiceResponse>>builder()
                .message("Filter default service successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "thêm dịch vụ mặc định")
    @PostMapping
    public ApiResponse<DefaultServiceResponse> createDefaultService (@Valid @RequestBody DefaultServiceCreationRequest request){
        return ApiResponse.<DefaultServiceResponse>builder()
                .message("Default Service has been created!")
                .data(defaultServiceService.createDefaultService(request))
                .build();
    }

    @Operation(summary = "Cập nhật dịch vụ mặc định")
    @PutMapping("/{defaultServiceId}")
    public ApiResponse<DefaultServiceResponse> updateService(
            @Valid @RequestBody DefaultServiceUpdateRequest request, @PathVariable("defaultServiceId") String defaultServiceId) {
        return ApiResponse.<DefaultServiceResponse>builder()
                .message("Default Service updated!")
                .data(defaultServiceService.updateDefaultService(defaultServiceId, request))
                .build();
    }

    @Operation(summary = "Xóa dich vụ mặc định")
    @DeleteMapping("/{defaultServiceId}")
    public ApiResponse<String> deleteDefaultServiceById (@PathVariable("defaultServiceId") String defaultServiceId) {
        defaultServiceService.deleteDefaultServiceById(defaultServiceId);
        return ApiResponse.<String>builder().message("Default Service has been deleted!").build();
    }



}
