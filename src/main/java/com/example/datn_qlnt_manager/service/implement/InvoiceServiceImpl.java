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
    UserService userService;
    CodeGeneratorService codeGeneratorService;
    InvoiceMapper invoiceMapper;
    InvoiceDetailsMapper invoiceDetailsMapper;
    PaymentReceiptRepository paymentReceiptRepository;
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

        int month =
                Optional.ofNullable(request.getMonth()).orElse(LocalDate.now().getMonthValue());
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

        InvoiceStatus status = totalAmount.compareTo(BigDecimal.ZERO) == 0
                ? InvoiceStatus.DA_THANH_TOAN
                : InvoiceStatus.CHUA_THANH_TOAN;

        String invoiceCode = codeGeneratorService.generateInvoiceCode(contract.getRoom(), month, year);

        String note = StringUtils.hasText(request.getNote())
                ? request.getNote()
                : "Hóa đơn tháng " + month + " năm " + year + " cho phòng "
                        + contract.getRoom().getRoomCode();

        Invoice invoice = Invoice.builder()
                .contract(contract)
                .invoiceCode(invoiceCode)
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

        // Lấy month, year với giá trị mặc định nếu null
        int month =
                Optional.ofNullable(request.getMonth()).orElse(LocalDate.now().getMonthValue());
        int year = Optional.ofNullable(request.getYear()).orElse(LocalDate.now().getYear());

        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime startOfMonth = ym.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = ym.atEndOfMonth().atTime(LocalTime.MAX);

        List<Contract> activeContracts = contractRepository.findActiveContractsByBuildingAndMonthYear(
                building.getId(), startOfMonth, endOfMonth);

        LocalDate today = LocalDate.now();
        LocalDate paymentDueDate =
                Optional.ofNullable(request.getPaymentDueDate()).orElse(LocalDate.of(year, month, 5));

        // Valid hạn thanh toán
        YearMonth thisMonth = YearMonth.from(today);
        YearMonth nextMonth = thisMonth.plusMonths(1);

        if (paymentDueDate.isBefore(today)) {
            throw new AppException(ErrorCode.PAYMENT_DUE_DATE_IN_PAST);
        }
        if (YearMonth.from(paymentDueDate).isAfter(nextMonth)) {
            throw new AppException(ErrorCode.PAYMENT_DUE_DATE_TOO_FAR);
        }

        String note =
                StringUtils.hasText(request.getNote()) ? request.getNote() : "Hóa đơn tháng " + month + " năm " + year;

        if (activeContracts.isEmpty()) {
            throw new AppException(ErrorCode.NO_ACTIVE_CONTRACT_FOUND);
        }

        List<InvoiceResponse> responses = new ArrayList<>();

        for (Contract contract : activeContracts) {
            // kiểm tra phòng có dịch vụ tính theo số và đã ghi chỉ số tháng này chưa
            if (!hasAllMeterReadingsForMonth(contract, month, year)) {
                continue; // chưa có thì bỏ qua phòng này
            }

            InvoiceCreationRequest roomRequest = InvoiceCreationRequest.builder()
                    .contractId(contract.getId())
                    .month(month)
                    .year(year)
                    .paymentDueDate(paymentDueDate)
                    .note(note)
                    .build();

            try {
                InvoiceResponse invoiceResponse = generateInvoiceForRoom(roomRequest);
                responses.add(invoiceResponse);
            } catch (AppException ex) {
                log.warn(
                        "Không thể tạo hóa đơn cho phòng {}: {}",
                        contract.getRoom().getRoomCode(),
                        ex.getMessage());
            }
        }

        return responses;
    }

    private InvoiceType resolveInvoiceType(Contract contract, int month, int year) {
        boolean isLastMonth = isEndMonth(contract, month, year);

        boolean monthlyInvoiceExists = invoiceRepository.existsByContractIdAndMonthAndYearAndInvoiceType(
                contract.getId(), month, year, InvoiceType.HANG_THANG);

        boolean finalInvoiceExists = invoiceRepository.existsByContractIdAndMonthAndYearAndInvoiceType(
                contract.getId(), month, year, InvoiceType.CUOI_CUNG);

        if (isLastMonth) {
            if (!monthlyInvoiceExists) {
                return InvoiceType.HANG_THANG;
            } else {
                if (finalInvoiceExists) {
                    throw new AppException(ErrorCode.INVOICE_ALREADY_EXISTS);
                }
                return InvoiceType.CUOI_CUNG;
            }
        } else {
            if (monthlyInvoiceExists) {
                throw new AppException(ErrorCode.INVOICE_ALREADY_EXISTS);
            }
            return InvoiceType.HANG_THANG;
        }
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
    private boolean hasAllMeterReadingsForMonth(Contract contract, int month, int year) {
        List<ServiceRoom> serviceRooms =
                serviceRoomRepository.findByRoomId(contract.getRoom().getId());

        for (ServiceRoom sr : serviceRooms) {
            if (sr.getService().getServiceCalculation() == ServiceCalculation.TINH_THEO_SO) {
                Optional<Meter> meterOpt = meterRepository.findByRoomIdAndServiceId(
                        contract.getRoom().getId(), sr.getService().getId());
                if (meterOpt.isEmpty()) {
                    return false;
                }

                Optional<MeterReading> readingOpt = meterReadingRepository.findByMeterIdAndMonthAndYear(
                        meterOpt.get().getId(), month, year);
                if (readingOpt.isEmpty()) {
                    return false; // chưa ghi chỉ số tháng này
                }
            }
        }
        return true; // tất cả dịch vụ tính theo số đã có chỉ số
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

    private List<InvoiceDetail> buildInvoiceDetailsItems(Invoice invoice, List<InvoiceItemResponse> items) {
        Room room = invoice.getContract().getRoom();
        List<Meter> meters = meterRepository.findByRoomId(room.getId());

        List<ServiceRoom> serviceRooms = serviceRoomRepository.findActiveByRoomIdAndMonth(
                room.getId(), LocalDate.of(invoice.getYear(), invoice.getMonth(), 1));

        Map<String, ServiceRoom> serviceRoomMap = serviceRooms.stream()
                .filter(sr -> sr.getService() != null && sr.getService().getId() != null)
                .collect(Collectors.toMap(sr -> sr.getService().getId(), sr -> sr, (a, b) -> a));

        Map<String, MeterType> meterServiceMap = meters.stream()
                .filter(m -> m.getService() != null && m.getService().getId() != null)
                .collect(Collectors.toMap(m -> m.getService().getId(), Meter::getMeterType, (a, b) -> a));

        return items.stream()
                .map(item -> {
                    ServiceCategory serviceCategory = item.getServiceCategory();
                    InvoiceItemType invoiceItemType;
                    ServiceRoom serviceRoom = null;

                    if (serviceCategory == null) {
                        throw new AppException(ErrorCode.INVALID_SERVICE_TYPE);
                    }

                    switch (serviceCategory) {
                        case TIEN_PHONG -> invoiceItemType = InvoiceItemType.TIEN_PHONG;

                        case DIEN -> {
                            invoiceItemType = InvoiceItemType.DIEN;
                            String serviceId = findServiceIdByItem(serviceRoomMap, item.getServiceName());
                            serviceRoom = serviceRoomMap.get(serviceId);
                        }

                        case NUOC -> {
                            String serviceId = findServiceIdByItem(serviceRoomMap, item.getServiceName());
                            serviceRoom = serviceRoomMap.get(serviceId);

                            // NUOC có thể là theo số hoặc theo người
                            MeterType meterType = meterServiceMap.get(serviceId);
                            invoiceItemType =
                                    (meterType == MeterType.NUOC) ? InvoiceItemType.NUOC : InvoiceItemType.DICH_VU;
                        }

                        default -> {
                            invoiceItemType = InvoiceItemType.DICH_VU;
                            String serviceId = findServiceIdByItem(serviceRoomMap, item.getServiceName());
                            serviceRoom = serviceRoomMap.get(serviceId);
                        }
                    }

                    BigDecimal unitPrice =
                            Optional.ofNullable(item.getUnitPrice()).orElse(BigDecimal.ZERO);
                    int quantity = Optional.ofNullable(item.getQuantity()).orElse(1);
                    BigDecimal amount = unitPrice.multiply(BigDecimal.valueOf(quantity));

                    InvoiceDetail details = InvoiceDetail.builder()
                            .invoice(invoice)
                            .invoiceItemType(invoiceItemType)
                            .serviceRoom(serviceRoom)
                            .serviceName(item.getServiceName())
                            .oldIndex(item.getOldIndex())
                            .newIndex(item.getNewIndex())
                            .quantity(item.getQuantity())
                            .unitPrice(item.getUnitPrice())
                            .amount(amount)
                            .description(
                                    item.getServiceName() + " tháng " + invoice.getMonth() + "/" + invoice.getYear())
                            .build();
                    details.setCreatedAt(Instant.now());
                    details.setUpdatedAt(Instant.now());

                    return details;
                })
                .toList();
    }

    private List<Contract> getValidContracts(Integer monthInput, Integer yearInput) {
        int month = Optional.ofNullable(monthInput).orElse(LocalDate.now().getMonthValue());
        int year = Optional.ofNullable(yearInput).orElse(LocalDate.now().getYear());

        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth());
        LocalDateTime startDateTime = startOfMonth.atStartOfDay();
        LocalDateTime endDateTime = endOfMonth.atTime(LocalTime.MAX);

        return contractRepository.findValidContractsInMonth(startDateTime, endDateTime).stream()
                .filter(c -> !invoiceRepository.existsByContractIdAndMonthAndYear(c.getId(), month, year))
                .toList();
    }

    private Invoice buildGenericInvoice(
            Contract contract,
            int month,
            int year,
            List<InvoiceItemResponse> items,
            String note,
            LocalDate paymentDueDate,
            InvoiceType invoiceType) {
        Room room = contract.getRoom();
        String invoiceCode = codeGeneratorService.generateInvoiceCode(room, month, year);
        String finalNote = StringUtils.hasText(note)
                ? note
                : "Hóa đơn tháng " + month + " năm " + year + " cho phòng " + room.getRoomCode();

        Invoice invoice = Invoice.builder()
                .contract(contract)
                .invoiceCode(invoiceCode)
                .month(month)
                .year(year)
                .paymentDueDate(paymentDueDate)
                .invoiceStatus(InvoiceStatus.CHUA_THANH_TOAN)
                .invoiceType(invoiceType)
                .note(finalNote)
                .build();

        List<InvoiceDetail> details = buildInvoiceDetailsItems(invoice, items);
        invoice.setDetails(details);

        BigDecimal totalAmount = details.stream()
                .map(InvoiceDetail::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        invoice.setTotalAmount(totalAmount);
        invoice.setCreatedAt(Instant.now());
        invoice.setUpdatedAt(Instant.now());

        return invoice;
    }

    private List<InvoiceResponse> createInvoicesByContracts(
            List<Contract> contracts,
            Integer monthInput,
            Integer yearInput,
            LocalDate paymentDueDateInput,
            String noteInput) {
        int month = Optional.ofNullable(monthInput).orElse(LocalDate.now().getMonthValue());
        int year = Optional.ofNullable(yearInput).orElse(LocalDate.now().getYear());
        LocalDate paymentDueDate = Optional.ofNullable(paymentDueDateInput).orElse(LocalDate.of(year, month, 5));

        validatePaymentDueDate(paymentDueDate, year, month);

        List<InvoiceResponse> responses = new ArrayList<>();
        for (Contract contract : contracts) {
            try {
                List<InvoiceItemResponse> items = getInvoiceItemsByContext(contract, contract.getRoom(), month, year);
                Invoice invoice = buildGenericInvoice(
                        contract, month, year, items, noteInput, paymentDueDate, InvoiceType.HANG_THANG);
                invoiceRepository.save(invoice);
                responses.add(invoiceMapper.toInvoiceResponse(invoice));
            } catch (Exception e) {
                log.error("Không thể tạo hóa đơn cho hợp đồng: {}", contract.getId(), e);
                throw new AppException(ErrorCode.INVOICE_CREATION_FAILED);
            }
        }
        return responses;
    }

    private List<InvoiceItemResponse> getInvoiceItemsByContext(Contract contract, Room room, int month, int year) {
        List<InvoiceItemResponse> items = new ArrayList<>();

        LocalDate contractStart = contract.getStartDate();
        // tháng đầu tiên không tính tiền điện, nước (dịch vụ tính theo số)
        boolean isFirstMonth = contractStart.getMonthValue() == month && contractStart.getYear() == year;

        items.add(buildRoomCharge(contract));
        items.addAll(buildNonMeterServiceCharges(room, contract, month, year));

        // Nếu không phải tháng đầu tiên => thêm tiền điện nước tháng trước
        if (!isFirstMonth) {
            int prevMonth = month == 1 ? 12 : month - 1;
            int prevYear = month == 1 ? year - 1 : year;
            items.addAll(buildElectricWaterCharges(room, contract, prevMonth, prevYear));
        }

        return items;
    }

    // Tính tiền điện & nước theo tháng
    private List<InvoiceItemResponse> buildElectricWaterCharges(Room room, Contract contract, int month, int year) {
        List<InvoiceItemResponse> items = new ArrayList<>();
        List<Meter> meters = meterRepository.findByRoomId(room.getId());

        for (Meter meter : meters) {
            Service service = meter.getService();
            if (service == null || !isMeterService(service)) continue;

            MeterReading meterReading = meterReadingRepository
                    .findByMeterIdAndMonthAndYear(meter.getId(), month, year)
                    .orElseThrow(() -> new AppException(ErrorCode.METER_READING_NOT_FOUND));

            items.add(buildMeterCharge(meter, meterReading, service, contract));
        }
        return items;
    }

    /// Tính tiền dịch vụ cố định/tháng
    private List<InvoiceItemResponse> buildNonMeterServiceCharges(Room room, Contract contract, int month, int year) {
        List<InvoiceItemResponse> items = new ArrayList<>();

        int numberOfPeople = contract.getContractTenants().size();
        int vehicleNumber =
                contract.getContractVehicles() != null ? contract.getContractVehicles().size() : 0;

        List<ServiceRoom> serviceRooms =
                serviceRoomRepository.findActiveByRoomIdAndMonth(room.getId(), LocalDate.of(year, month, 1));

        for (ServiceRoom serviceRoom : serviceRooms) {
            Service service = serviceRoom.getService();
            if (service == null) continue;

            ServiceCalculation calculation = service.getServiceCalculation();
            ServiceCategory category = service.getServiceCategory();
            BigDecimal donGia = service.getPrice();

            int quantity;

            switch (calculation) {
                case TINH_THEO_PHONG -> quantity = 1;
                case TINH_THEO_NGUOI -> quantity = numberOfPeople;
                case TINH_THEO_PHUONG_TIEN -> quantity = vehicleNumber;
                default -> {
                    continue;
                }
            }

            BigDecimal amount = donGia.multiply(BigDecimal.valueOf(quantity));

            items.add(InvoiceItemResponse.builder()
                    .serviceName(service.getName())
                    .serviceCategory(category)
                    .serviceCalculation(calculation)
                    .quantity(quantity)
                    .unitPrice(donGia)
                    .amount(amount)
                    .build());
        }
        return items;
    }

    private BigDecimal resolveMeterUnitPrice(Meter meter, Service service, Contract contract) {
        return switch (meter.getMeterType()) {
            case DIEN -> contract.getElectricPrice() != null ? contract.getElectricPrice() : service.getPrice();
            case NUOC -> contract.getWaterPrice() != null ? contract.getWaterPrice() : service.getPrice();
        };
    }

    // Tính tiền phòng
    private InvoiceItemResponse buildRoomCharge(Contract contract) {
        BigDecimal unitPrice = contract.getRoomPrice();

        return InvoiceItemResponse.builder()
                .serviceName("Tiền phòng")
                .serviceCategory(ServiceCategory.TIEN_PHONG)
                .serviceCalculation(ServiceCalculation.TINH_THEO_PHONG)
                .quantity(1)
                .unitPrice(unitPrice)
                .unit("phòng")
                .amount(unitPrice)
                .build();
    }

    // Tính tiền điện & nước
    private InvoiceItemResponse buildMeterCharge(
            Meter meter, MeterReading meterReading, Service service, Contract contract) {
        int quantity = meterReading.getQuantity();
        BigDecimal unitPrice = resolveMeterUnitPrice(meter, service, contract);

        return InvoiceItemResponse.builder()
                .serviceName(service.getName())
                .serviceCategory(service.getServiceCategory())
                .serviceCalculation(service.getServiceCalculation())
                .oldIndex(meterReading.getOldIndex())
                .newIndex(meterReading.getNewIndex())
                .quantity(quantity)
                .unitPrice(unitPrice)
                .unit(service.getUnit())
                .amount(unitPrice.multiply(BigDecimal.valueOf(quantity)))
                .build();
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

    private void validatePaymentDueDate(LocalDate dueDate, int year, int month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfNextMonth = startOfMonth.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());

        if (dueDate.isBefore(startOfMonth) || dueDate.isAfter(endOfNextMonth)) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_DUE_DATE);
        }
    }

    private boolean isMeterService(Service service) {
        // Chỉ lấy những dịch vụ có đồng hồ đo (điện luôn có, nước có thể)
        return service.getServiceCalculation() == ServiceCalculation.TINH_THEO_SO
                && (service.getServiceCategory() == ServiceCategory.DIEN
                        || service.getServiceCategory() == ServiceCategory.NUOC);
    }

    private String findServiceIdByItem(Map<String, ServiceRoom> serviceRoomMap, String serviceName) {
        return serviceRoomMap.values().stream()
                .filter(sr -> sr.getService().getName().equals(serviceName))
                .map(sr -> sr.getService().getId())
                .findFirst()
                .orElse(null);
    }
}
