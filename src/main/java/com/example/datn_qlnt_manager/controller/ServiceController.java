package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.common.ServiceStatus;
import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.ServiceFilter;
import com.example.datn_qlnt_manager.dto.request.service.ServiceCreationRequest;
import com.example.datn_qlnt_manager.dto.request.service.ServiceUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.room.RoomCountResponse;
import com.example.datn_qlnt_manager.dto.response.service.ServiceCountResponse;
import com.example.datn_qlnt_manager.dto.response.service.ServiceResponse;
import com.example.datn_qlnt_manager.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Service", description = "API Room")
public class ServiceController {
    ServiceService serviceService;

    @Operation(summary = "Phân trang danh sách dịch vụ")
    @GetMapping
    public ApiResponse<List<ServiceResponse>> getPageAndSearchAndFilterServices(
            @ModelAttribute ServiceFilter serviceFilter,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "15") Integer size) {

        PaginatedResponse<ServiceResponse> result = serviceService.getPageAndSearchAndFilterService(serviceFilter, page, size);

        return ApiResponse.<List<ServiceResponse>>builder()
                .message("Lấy danh sách dịch vụ thành công")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }


    @PostMapping
    public ApiResponse<ServiceResponse> createService(@RequestBody @Valid ServiceCreationRequest request) {
        return ApiResponse.<ServiceResponse>builder()
                .data(serviceService.createService(request))
                .message("Add service success")
                .code(201)
                .build();
    }

    @PutMapping("/update/{id}")
    public ApiResponse<ServiceResponse> updateService(
            @PathVariable("id") String serviceId, @RequestBody @Valid ServiceUpdateRequest request) {
        return ApiResponse.<ServiceResponse>builder()
                .data(serviceService.updateService(serviceId, request))
                .message("Update service success")
                .code(200)
                .build();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteService(@PathVariable("id") String serviceId) {
        return ApiResponse.<Void>builder()
                .data(serviceService.deleteService(serviceId))
                .message("Delete service success")
                .code(200)
                .build();
    }

    @PutMapping("/soft-delete/{id}")
    public ApiResponse<Void> softDeleteService(@PathVariable("id") String id) {
        serviceService.softDeleteServiceById(id);
        return ApiResponse.<Void>builder()
                .message("Soft delete service success.")
                .code(200)
                .build();
    }

    @GetMapping("/statistics")
    public ApiResponse<ServiceCountResponse> statisticsServiceByStatus() {
        return ApiResponse.<ServiceCountResponse>builder()
                .message("Count service success!")
                .data(serviceService.statisticsServiceByStatus())
                .build();
    }

    @PutMapping("/toggle-status/{id}")
    public ApiResponse<ServiceResponse> toggleServiceStatus(
            @PathVariable("id") String serviceId) {

        ServiceResponse response = serviceService.toggleServiceStatus(serviceId);

        return ApiResponse.<ServiceResponse>builder()
                .message("Cập nhật trạng thái dịch vụ thành công")
                .data(response)
                .code(200)
                .build();
    }


}
