package com.example.datn_qlnt_manager.service.implement;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.datn_qlnt_manager.common.*;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.InvoiceFilter;
import com.example.datn_qlnt_manager.dto.projection.InvoiceDetailView;
import com.example.datn_qlnt_manager.dto.request.invoice.InvoiceBuildingCreationRequest;
import com.example.datn_qlnt_manager.dto.request.invoice.InvoiceCreationRequest;
import com.example.datn_qlnt_manager.dto.request.invoice.InvoiceUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceDetailsResponse;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceItemResponse;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceResponse;
import com.example.datn_qlnt_manager.dto.statistics.InvoiceStatistics;
import com.example.datn_qlnt_manager.entity.*;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.InvoiceDetailsMapper;
import com.example.datn_qlnt_manager.mapper.InvoiceMapper;
import com.example.datn_qlnt_manager.repository.*;
import com.example.datn_qlnt_manager.service.InvoiceChargeCalculatorService;
import com.example.datn_qlnt_manager.service.InvoiceService;
import com.example.datn_qlnt_manager.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceServiceImpl implements InvoiceService {
    InvoiceRepository invoiceRepository;
    InvoiceDetailsRepository invoiceDetailsRepository;
    MeterRepository meterRepository;
    MeterReadingRepository meterReadingRepository;
    ServiceRoomRepository serviceRoomRepository;
    ContractRepository contractRepository;
    BuildingRepository buildingRepository;
    PaymentReceiptRepository paymentReceiptRepository;
    ContractTenantRepository contractTenantRepository;
    UserService userService;
    CodeGeneratorService codeGeneratorService;
    InvoiceMapper invoiceMapper;
    InvoiceDetailsMapper invoiceDetailsMapper;
    InvoiceChargeCalculatorService invoiceChargeCalculatorService;

    @Override
    public PaginatedResponse<InvoiceResponse> getPageAndSearchAndFilterByUserId(
            InvoiceFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        var user = userService.getCurrentUser();

        Page<Invoice> paging = invoiceRepository.getPageAnsSearchAndFilterInvoiceByOwnerId(
                user.getId(),
                filter.getQuery(),
                filter.getBuilding(),
                filter.getFloor(),
                filter.getMonth(),
                filter.getYear(),
                filter.getMinTotalAmount(),
                filter.getMaxTotalAmount(),
                filter.getInvoiceStatus(),
                filter.getInvoiceType(),
                pageable);

        return buildPaginatedInvoiceResponse(paging, page, size);
    }

    @Override
    public PaginatedResponse<InvoiceResponse> getInvoiceWithStatusCancelByUserId(
            InvoiceFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        var user = userService.getCurrentUser();

        Page<Invoice> paging = invoiceRepository.getInvoiceWithStatusCancelByUserId(
                user.getId(),
                filter.getQuery(),
                filter.getBuilding(),
                filter.getFloor(),
                filter.getMonth(),
                filter.getYear(),
                filter.getMinTotalAmount(),
                filter.getMaxTotalAmount(),
                filter.getInvoiceType(),
                pageable);

        return buildPaginatedInvoiceResponse(paging, page, size);
    }

    @Override
    public InvoiceDetailsResponse getInvoiceDetails(String invoiceId) {
        InvoiceDetailView detailView = invoiceRepository
                .getInvoiceDetailById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));

        List<InvoiceItemResponse> items = invoiceDetailsRepository.findByInvoiceId(invoiceId).stream()
                .map(invoiceMapper::toItemResponse)
                .toList();

        return invoiceDetailsMapper.toResponse(detailView, items);
    }

    @Transactional
    @Override
    public InvoiceResponse generateInvoiceForRoom(InvoiceCreationRequest request) {
        Contract contract = contractRepository
                .findById(request.getContractId())
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        ContractStatus contractStatus = contract.getStatus();
        if (!(contractStatus == ContractStatus.HIEU_LUC
                || contractStatus == ContractStatus.SAP_HET_HAN
                || contractStatus == ContractStatus.TU_Y_HUY_BO)) {
            throw new AppException(ErrorCode.CONTRACT_NOT_ACTIVE);
        }

        int month = Optional.ofNullable(request.getMonth()).orElse(LocalDate.now().getMonthValue());
        int year = Optional.ofNullable(request.getYear()).orElse(LocalDate.now().getYear());

        if (!isContractActiveDuring(contract, month, year)) {
            throw new AppException(ErrorCode.CONTRACT_NOT_ACTIVE);
        }

        InvoiceType invoiceType = resolveInvoiceType(contract, month, year);
        LocalDate paymentDueDate = resolvePaymentDueDate(request, invoiceType);

        List<InvoiceItemResponse> invoiceItems = invoiceChargeCalculatorService.generateInvoiceItems(
                contract, month, year, invoiceType, Collections.emptyList());

        BigDecimal totalAmount = invoiceItems.stream()
                .map(InvoiceItemResponse::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        InvoiceStatus status;
        if (contract.getStatus() == ContractStatus.TU_Y_HUY_BO) {
            status = InvoiceStatus.KHONG_THE_THANH_TOAN;
        } else {
            status = totalAmount.compareTo(BigDecimal.ZERO) == 0
                    ? InvoiceStatus.DA_THANH_TOAN
                    : InvoiceStatus.CHUA_THANH_TOAN;
        }

        String invoiceCode = codeGeneratorService.generateInvoiceCode(contract.getRoom(), month, year);

        String  ownerPhoneNumber = contract.getRoom().getFloor().getBuilding().getUser().getPhoneNumber();

        Building building = contract.getRoom().getFloor().getBuilding();

        String roomCode = contract.getRoom().getRoomCode();

        ContractTenant contractTenant = contractTenantRepository
                .findByContractIdAndRepresentativeTrue(contract.getId())
                .orElseThrow(() -> new AppException(ErrorCode.REPRESENTATIVE_NOT_FOUND));

        Tenant representative = contractTenant.getTenant();

        String note = StringUtils.hasText(request.getNote())
                ? request.getNote()
                : "Hóa đơn tháng " + month + " năm " + year + " cho phòng "
                        + contract.getRoom().getRoomCode();

        Invoice invoice = Invoice.builder()
                .contract(contract)
                .invoiceCode(invoiceCode)
                .ownerPhoneNumber(ownerPhoneNumber)
                .buildingName(building.getBuildingName())
                .buildingAddress(building.getAddress())
                .roomCode(roomCode)
                .tenantName(representative.getFullName())
                .tenantPhoneNumber(representative.getPhoneNumber())
                .totalAmount(totalAmount)
                .month(month)
                .year(year)
                .paymentDueDate(paymentDueDate)
                .invoiceStatus(status)
                .invoiceType(invoiceType)
                .note(note)
                .build();

        invoice.setCreatedAt(Instant.now());
        invoice.setUpdatedAt(Instant.now());

        invoiceRepository.save(invoice);

        List<InvoiceDetail> invoiceDetails = invoiceItems.stream()
                .map(item -> mapToInvoiceDetail(item, invoice))
                .collect(Collectors.toList());

        invoiceDetailsRepository.saveAll(invoiceDetails);
        invoice.setDetails(invoiceDetails);

        return invoiceMapper.toInvoiceResponse(invoice);
    }

    @Transactional
    @Override
    public List<InvoiceResponse> generateInvoicesForBuilding(InvoiceBuildingCreationRequest request) {
        Building building = buildingRepository
                .findById(request.getBuildingId())
                .orElseThrow(() -> new AppException(ErrorCode.BUILDING_NOT_FOUND));

        int month = Optional.ofNullable(request.getMonth()).orElse(LocalDate.now().getMonthValue());
        int year = Optional.ofNullable(request.getYear()).orElse(LocalDate.now().getYear());

        YearMonth ym = YearMonth.of(year, month);
        LocalDate startOfMonth = ym.atDay(1);
        LocalDate endOfMonth = ym.atEndOfMonth();

        List<Contract> activeContracts =
                contractRepository.findActiveContractsByBuildingAndMonthYear(building.getId(), startOfMonth, endOfMonth);

        if (activeContracts.isEmpty()) {
            throw new AppException(ErrorCode.NO_ACTIVE_CONTRACT_FOUND);
        }

        LocalDate today = LocalDate.now();
        LocalDate paymentDueDate =
                Optional.ofNullable(request.getPaymentDueDate()).orElse(LocalDate.of(year, month, 5));

        YearMonth thisMonth = YearMonth.from(today);
        YearMonth nextMonth = thisMonth.plusMonths(1);

        if (paymentDueDate.isBefore(today)) {
            throw new AppException(ErrorCode.PAYMENT_DUE_DATE_IN_PAST);
        }
        if (YearMonth.from(paymentDueDate).isAfter(nextMonth)) {
            throw new AppException(ErrorCode.PAYMENT_DUE_DATE_TOO_FAR);
        }

        String note = StringUtils.hasText(request.getNote())
                ? request.getNote()
                : "Hóa đơn tháng " + month + " năm " + year;

        List<InvoiceResponse> responses = new ArrayList<>();

        for (Contract contract : activeContracts) {
            try {
                ContractStatus contractStatus = contract.getStatus();
                if (!(contractStatus == ContractStatus.HIEU_LUC
                        || contractStatus == ContractStatus.SAP_HET_HAN
                        || contractStatus == ContractStatus.TU_Y_HUY_BO)) {
                    continue;
                }

                InvoiceType invoiceType = resolveInvoiceType(contract, month, year);

                if (!hasAllMeterReadingsForMonth(contract, month, year, invoiceType)) {
                    continue; // phòng không có chỉ số tháng này thì bỏ qua
                }

                InvoiceCreationRequest roomRequest = InvoiceCreationRequest.builder()
                        .contractId(contract.getId())
                        .month(month)
                        .year(year)
                        .paymentDueDate(paymentDueDate)
                        .note(note)
                        .build();

                InvoiceResponse invoiceResponse = generateInvoiceForRoom(roomRequest);
                responses.add(invoiceResponse);

            } catch (AppException ex) {
                log.warn("Không thể tạo hóa đơn cho phòng {}: {}",
                        contract.getRoom().getRoomCode(),
                        ex.getMessage());
            }
        }

        return responses;
    }


    private InvoiceType resolveInvoiceType(Contract contract, int month, int year) {
        boolean isLastMonth = isEndMonth(contract, month, year);

        boolean monthlyExists = invoiceRepository.existsByContractIdAndMonthAndYearAndInvoiceType(
                contract.getId(), month, year, InvoiceType.HANG_THANG);

        boolean finalExists = invoiceRepository.existsByContractIdAndMonthAndYearAndInvoiceType(
                contract.getId(), month, year, InvoiceType.CUOI_CUNG);

        if (isLastMonth) {
            if (!monthlyExists) {
                return InvoiceType.HANG_THANG;
            }
            if (finalExists) {
                throw new AppException(ErrorCode.INVOICE_ALREADY_EXISTS);
            }
            return InvoiceType.CUOI_CUNG;
        }

        if (monthlyExists) {
            throw new AppException(ErrorCode.INVOICE_ALREADY_EXISTS);
        }
        return InvoiceType.HANG_THANG;
    }


    private InvoiceDetail mapToInvoiceDetail(InvoiceItemResponse item, Invoice invoice) {

        ServiceRoom serviceRoom = null;
        if (item.getServiceRoomId() != null) {
            serviceRoom = serviceRoomRepository
                    .findById(item.getServiceRoomId())
                    .orElseThrow(() -> new AppException(ErrorCode.SERVICE_ROOM_NOT_FOUND));
        }

        InvoiceDetail detail = InvoiceDetail.builder()
                .invoice(invoice)
                .invoiceItemType(resolveInvoiceItemType(item.getServiceCategory()))
                .serviceRoom(serviceRoom)
                .serviceName(item.getServiceName())
                .oldIndex(item.getOldIndex())
                .newIndex(item.getNewIndex())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .amount(item.getAmount())
                .description(item.getDescription())
                .build();

        detail.setCreatedAt(Instant.now());
        detail.setUpdatedAt(Instant.now());

        return detail;
    }

    private InvoiceItemType resolveInvoiceItemType(ServiceCategory category) {
        return switch (category) {
            case DIEN -> InvoiceItemType.DIEN;
            case NUOC -> InvoiceItemType.NUOC;
            case TIEN_PHONG -> InvoiceItemType.TIEN_PHONG;
            case DEN_BU -> InvoiceItemType.DEN_BU;
            default -> InvoiceItemType.DICH_VU;
        };
    }

    // kiểm tra xem hợp đồng có dịch vụ tính theo số và đã có chỉ số tháng này chưa
    private boolean hasAllMeterReadingsForMonth(Contract contract, int month, int year, InvoiceType invoiceType) {
        List<ServiceRoom> serviceRooms = serviceRoomRepository.findByRoomId(contract.getRoom().getId());

        for (ServiceRoom sr : serviceRooms) {
            if (sr.getService().getServiceCalculation() == ServiceCalculation.TINH_THEO_SO) {
                // Tìm công tơ của dịch vụ
                Meter meter = meterRepository.findByRoomIdAndServiceId(
                        contract.getRoom().getId(), sr.getService().getId()
                ).orElse(null);

                if (meter == null) {
                    return false;
                }

                int checkMonth;
                int checkYear;

                if (invoiceType == InvoiceType.HANG_THANG) {
                    // lấy chỉ số tháng trước
                    YearMonth prev = YearMonth.of(year, month).minusMonths(1);
                    checkMonth = prev.getMonthValue();
                    checkYear = prev.getYear();
                } else if (invoiceType == InvoiceType.CUOI_CUNG) {
                    // lấy chỉ số tháng hiện tại
                    checkMonth = month;
                    checkYear = year;
                } else {
                    continue; // các loại khác (nếu có) thì bỏ qua
                }

                Optional<MeterReading> readingOpt =
                        meterReadingRepository.findByMeterIdAndMonthAndYear(meter.getId(), checkMonth, checkYear);

                if (readingOpt.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private LocalDate resolvePaymentDueDate(InvoiceCreationRequest request, InvoiceType invoiceType) {
        LocalDate now = LocalDate.now();

        LocalDate dueDate;
        if (request.getPaymentDueDate() != null) {
            dueDate = request.getPaymentDueDate();
        } else {
            if (invoiceType == InvoiceType.HANG_THANG) {
                dueDate = LocalDate.of(request.getYear(), request.getMonth(), 5);
            } else { // CUOI_KY
                dueDate = now;
            }
        }

        if (dueDate.isBefore(now)) {
            throw new AppException(ErrorCode.PAYMENT_DUE_DATE_IN_PAST);
        }

        YearMonth maxAllowed = YearMonth.from(now).plusMonths(1);
        if (YearMonth.from(dueDate).isAfter(maxAllowed)) {
            throw new AppException(ErrorCode.PAYMENT_DUE_DATE_TOO_FAR);
        }

        return dueDate;
    }

    private boolean isContractActiveDuring(Contract contract, int month, int year) {
        if (contract.getStartDate() == null) return false;

        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        LocalDate start = contract.getStartDate();
        LocalDate end = contract.getEndDate() != null ? contract.getEndDate() : null;

        boolean startedBeforeEndOfMonth = !start.isAfter(lastDay);
        boolean notEndedBeforeStartOfMonth = end == null || !end.isBefore(firstDay);

        return startedBeforeEndOfMonth && notEndedBeforeStartOfMonth;
    }

    private boolean isEndMonth(Contract contract, int month, int year) {
        if (contract.getEndDate() == null) return false;
        return contract.getEndDate().getYear() == year && contract.getEndDate().getMonthValue() == month;
    }

    @Transactional
    @Override
    public InvoiceResponse updateInvoice(String invoiceId, InvoiceUpdateRequest request) {
        Invoice invoice =
                invoiceRepository.findById(invoiceId).orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));

        if (invoice.getInvoiceStatus() != InvoiceStatus.CHUA_THANH_TOAN) {
            throw new AppException(ErrorCode.INVOICE_NOT_EDITABLE);
        }

        LocalDate dueDate = request.getPaymentDueDate();
        LocalDate now = LocalDate.now();
        LocalDate lastDayOfMonth =
                LocalDate.of(now.getYear(), now.getMonth(), 1).with(TemporalAdjusters.lastDayOfMonth());

        if (dueDate.isBefore(LocalDate.now()) || dueDate.isAfter(lastDayOfMonth)) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_DUE_DATE);
        }

        invoiceMapper.updateInvoice(request, invoice);
        invoice.setUpdatedAt(Instant.now());

        return invoiceMapper.toInvoiceResponse(invoiceRepository.save(invoice));
    }

    @Override
    public InvoiceStatistics getInvoiceStatistics() {
        User user = userService.getCurrentUser();

        return invoiceRepository.getTotalInvoiceByStatus(user.getId());
    }

    @Transactional
    @Override
    public void toggleInvoiceStatus(String invoiceId) {
        Invoice invoice =
                invoiceRepository.findById(invoiceId).orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));

        PaymentReceipt paymentReceipt = paymentReceiptRepository.findByInvoiceId(invoice.getId());

        List<InvoiceStatus> validStatuses =
                List.of(InvoiceStatus.CHUA_THANH_TOAN, InvoiceStatus.CHO_THANH_TOAN, InvoiceStatus.HUY);

        if (!validStatuses.contains(invoice.getInvoiceStatus())) {
            throw new AppException(ErrorCode.INVALID_INVOICE_STATUS);
        }

        invoice.setInvoiceStatus(InvoiceStatus.DA_THANH_TOAN);
        invoice.setUpdatedAt(Instant.now());

        paymentReceipt.setPaymentStatus(PaymentStatus.DA_THANH_TOAN);
        paymentReceipt.setUpdatedAt(Instant.now());

        invoiceRepository.save(invoice);
        paymentReceiptRepository.save(paymentReceipt);
    }

    @Transactional
    @Override
    public void softDeleteInvoice(String invoiceId) {
        Invoice invoice =
                invoiceRepository.findById(invoiceId).orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));

        if (invoice.getInvoiceStatus() != InvoiceStatus.HUY) {
            invoice.setInvoiceStatus(InvoiceStatus.HUY);

        } else {
            throw new AppException(ErrorCode.INVOICE_ALREADY_CANCELLED);
        }

        invoice.setUpdatedAt(Instant.now());

        invoiceRepository.save(invoice);
    }

    @Override
    public void deleteInvoice(String invoiceId) {
        Invoice invoice =
                invoiceRepository.findById(invoiceId).orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));

        if (invoice.getInvoiceStatus() != InvoiceStatus.HUY) {
            throw new AppException(ErrorCode.INVOICE_CAN_NOT_BE_DELETED);
        }

        invoiceRepository.delete(invoice);
    }

    @Override
    public List<InvoiceResponse> getAllInvoicesByUserId() {
        User user = userService.getCurrentUser();
        List<Invoice> invoices = invoiceRepository.findAllInvoicesByUserId(user.getId());

        return invoices.stream().map(invoiceMapper::toInvoiceResponse).toList();
    }

    @Override
    public PaginatedResponse<InvoiceResponse> getInvoicesForTenant(InvoiceFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        var user = userService.getCurrentUser();

        List<InvoiceStatus> statusList =
                List.of(InvoiceStatus.CHO_THANH_TOAN, InvoiceStatus.DA_THANH_TOAN, InvoiceStatus.QUA_HAN);

        Page<Invoice> paging = invoiceRepository.getInvoicesForTenant(
                user.getId(),
                filter.getQuery(),
                filter.getBuilding(),
                filter.getFloor(),
                filter.getMonth(),
                filter.getYear(),
                filter.getMinTotalAmount(),
                filter.getMaxTotalAmount(),
                filter.getInvoiceStatus(),
                filter.getInvoiceType(),
                statusList,
                pageable);

        return buildPaginatedInvoiceResponse(paging, page, size);
    }

    @Override
    public InvoiceResponse restoreInvoiceById(String invoiceId) {
        Invoice invoice =
                invoiceRepository.findById(invoiceId).orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));
        invoice.setInvoiceStatus(InvoiceStatus.DA_THANH_TOAN);
        return invoiceMapper.toInvoiceResponse(invoiceRepository.save(invoice));
    }

    private PaginatedResponse<InvoiceResponse> buildPaginatedInvoiceResponse(Page<Invoice> paging, int page, int size) {

        List<InvoiceResponse> invoices = paging.getContent().stream()
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
}
