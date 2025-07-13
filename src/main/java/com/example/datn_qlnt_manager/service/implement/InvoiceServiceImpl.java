package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.common.ServiceAppliedBy;
import com.example.datn_qlnt_manager.common.ServiceType;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.InvoiceFilter;
import com.example.datn_qlnt_manager.dto.projection.InvoiceDetailView;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceDetailsResponse;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceItemResponse;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceResponse;
import com.example.datn_qlnt_manager.entity.*;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.InvoiceMapper;
import com.example.datn_qlnt_manager.repository.*;
import com.example.datn_qlnt_manager.service.InvoiceService;
import com.example.datn_qlnt_manager.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceServiceImpl implements InvoiceService {
    InvoiceRepository invoiceRepository;
    MeterRepository meterRepository;
    MeterReadingRepository meterReadingRepository;
    ServiceRoomRepository serviceRoomRepository;
    UserService userService;
    InvoiceMapper invoiceMapper;

    @Override
    public PaginatedResponse<InvoiceResponse> getPageAndSearchAndFilterByUserId(
            InvoiceFilter filter,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        var user = userService.getCurrentUser();

        Page<Invoice> paging = invoiceRepository.getPageAnsSearchAndFilterInvoiceByOwnerId(
                user.getId(),
                filter.getQuery(),
                filter.getBuilding(),
                filter.getFloor(),
                filter.getMonth(),
                filter.getYear(),
                filter.getMinGrantTotal(),
                filter.getMaxGrantTotal(),
                filter.getInvoiceStatus(),
                pageable
        );

        return buildPaginatedInvoiceResponse(paging, page, size);
    }

    @Override
    public PaginatedResponse<InvoiceResponse> getInvoiceWithStatusCancelByUserId(
            InvoiceFilter filter,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        var user = userService.getCurrentUser();

        Page<Invoice> paging = invoiceRepository.getInvoiceWithStatusCancelByUserId(
                user.getId(),
                filter.getQuery(),
                filter.getBuilding(),
                filter.getFloor(),
                filter.getMonth(),
                filter.getYear(),
                filter.getMinGrantTotal(),
                filter.getMaxGrantTotal(),
                pageable
        );

        return buildPaginatedInvoiceResponse(paging, page, size);
    }

    @Override
    public InvoiceDetailsResponse getInvoiceDetails(String invoiceId) {
        InvoiceDetailView invoiceDetailView = invoiceRepository.getInvoiceDetailById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));

        List<InvoiceItemResponse> items = getInvoiceItems(invoiceId);

        BigDecimal totalAmount = items.stream()
                .map(InvoiceItemResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        InvoiceDetailsResponse response = invoiceMapper.toResponse(invoiceDetailView, items);
        response.setTotalAmount(totalAmount);

        return response;
    }

    private List<InvoiceItemResponse> getInvoiceItems(String invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));

        Contract contract = invoice.getContract();
        Room room = contract.getRoom();
        int numberOfPeople = contract.getNumberOfPeople();
        int month = invoice.getMonth();
        int year = invoice.getYear();

        List<InvoiceItemResponse> items = new ArrayList<>();

        items.add(buildRoomCharge(room));

        List<Meter> meters =  meterRepository.findByRoomId(room.getId());
        Set<String> meterServiceIds = meters.stream()
                .map(Meter::getService)
                .filter(Objects::nonNull)
                .map(Service::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (Meter meter : meters) {
            Service service = meter.getService();

            if (service == null) continue;

            MeterReading meterReading = meterReadingRepository
                    .findByMeterIdAndMonthAndYear(meter.getId(), month, year)
                    .orElseThrow(() -> new AppException(ErrorCode.METER_READING_NOT_FOUND));

            items.add(buildMeterCharge(meter, meterReading, service));
        }

        List<ServiceRoom> serviceRooms = serviceRoomRepository.findActiveByRoomIdAndMonth(
                room.getId(),
                LocalDate.of(year, month, 1)
        );

        for (ServiceRoom serviceRoom : serviceRooms) {
            Service service = serviceRoom.getService();

            if (service == null || meterServiceIds.contains(service.getId())) {
                continue;
            }
            items.add(buildServiceCharge(serviceRoom, service, numberOfPeople));
        }

        return items;
    }

    private PaginatedResponse<InvoiceResponse> buildPaginatedInvoiceResponse(
            Page<Invoice> paging, int page, int size) {

        List<InvoiceResponse> invoices = paging.getContent()
                .stream()
                .map(invoiceMapper::toInvoiceResponse)
                .toList();

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(paging.getNumberOfElements())
                        .perPage(size)
                        .currentPage(page)
                        .totalPages(paging.getTotalPages())
                        .total(paging.getTotalElements())
                        .build())
                .build();

        return PaginatedResponse.<InvoiceResponse>builder()
                .data(invoices)
                .meta(meta)
                .build();
    }

    // Tính tiền phòng
    private InvoiceItemResponse buildRoomCharge(Room room) {
        return InvoiceItemResponse.builder()
                .serviceName("Tiền phòng")
                .serviceType(ServiceType.TIEN_PHONG)
                .quantity(1)
                .unitPrice(room.getPrice())
                .amount(room.getPrice())
                .build();
    }

    //Tính tiền điện & nước
    private InvoiceItemResponse buildMeterCharge(
        Meter meter,
        MeterReading meterReading,
        Service service
    ) {
        int quantity = meterReading.getQuantity();
        BigDecimal unitPrice = service.getPrice();

        return InvoiceItemResponse.builder()
                .serviceName(service.getName())
                .serviceType(service.getType())
                .oldIndex(meterReading.getOldIndex())
                .newIndex(meterReading.getNewIndex())
                .quantity(quantity)
                .unitPrice(unitPrice)
                .amount(unitPrice.multiply(BigDecimal.valueOf(quantity)))
                .build();
    }

    // Tính tiền dịch vụ (cố định hoặc theo người)
    private InvoiceItemResponse buildServiceCharge(
            ServiceRoom serviceRoom,
            Service service,
            Integer numberOfPeople
    ) {
        int quantity;
        if (service.getAppliedBy() == ServiceAppliedBy.PHONG) {
            quantity = 1;
        } else if (service.getAppliedBy() == ServiceAppliedBy.NGUOI) {
            quantity = numberOfPeople;
        } else {
            throw new AppException(ErrorCode.INVALID_SERVICE_APPLIES);
        }

        BigDecimal unitPrice = service.getPrice();
        BigDecimal amount = unitPrice.multiply(BigDecimal.valueOf(quantity));

        return InvoiceItemResponse.builder()
                .serviceName(service.getName())
                .serviceType(service.getType())
                .quantity(quantity)
                .unitPrice(unitPrice)
                .amount(amount)
                .build();
    }

}
