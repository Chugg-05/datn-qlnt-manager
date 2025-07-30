package com.example.datn_qlnt_manager.service.implement;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import com.example.datn_qlnt_manager.dto.request.paymentReceipt.PaymentMethodRequest;
import com.example.datn_qlnt_manager.dto.request.paymentReceipt.RejectPaymentRequest;
import com.example.datn_qlnt_manager.dto.response.paymentReceipt.PaymentBatchResponse;
import com.example.datn_qlnt_manager.dto.response.paymentReceipt.PaymentMethodResponse;
import com.example.datn_qlnt_manager.dto.response.paymentReceipt.RejectPaymentResponse;
import com.example.datn_qlnt_manager.service.EmailService;
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
import com.example.datn_qlnt_manager.dto.request.invoice.InvoiceFloorCreationRequest;
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
    PaymentReceiptRepository paymentReceiptRepository;
    UserService userService;
    EmailService emailService;
    CodeGeneratorService codeGeneratorService;
    InvoiceMapper invoiceMapper;
    InvoiceDetailsMapper invoiceDetailsMapper;

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

        InvoiceDetailsResponse response = invoiceDetailsMapper.toResponse(detailView, items);
        response.setTotalAmount(calculateTotalAmount(items));
        return response;
    }

    @Transactional
    @Override
    public InvoiceResponse createInvoiceForContract(InvoiceCreationRequest request) {
        Contract contract = getValidContract(request.getContractId());

        int month = getMonthOrDefault(request.getMonth());
        int year = getYearOrDefault(request.getYear());
        LocalDate paymentDueDate = getPaymentDueDateOrDefault(request.getPaymentDueDate(), month, year);

        validateContractForInvoice(contract, month, year);
        validatePaymentDueDate(paymentDueDate, year, month);

        List<InvoiceItemResponse> items = getInvoiceItemsByContext(contract, contract.getRoom(), month, year);
        Invoice invoice = buildGenericInvoice(
                contract, month, year, items, request.getNote(), paymentDueDate, InvoiceType.HANG_THANG);

        return invoiceMapper.toInvoiceResponse(invoiceRepository.save(invoice));
    }

    @Transactional
    @Override
    public List<InvoiceResponse> createInvoicesForBuilding(InvoiceBuildingCreationRequest request) {
        return createInvoicesByContracts(
                getValidContractsByBuilding(request.getBuildingId(), request.getMonth(), request.getYear()),
                request.getMonth(),
                request.getYear(),
                request.getPaymentDueDate(),
                request.getNote());
    }

    @Transactional
    @Override
    public List<InvoiceResponse> createInvoicesForFloor(InvoiceFloorCreationRequest request) {
        return createInvoicesByContracts(
                getValidContractsByFloor(request.getFloorId(), request.getMonth(), request.getYear()),
                request.getMonth(),
                request.getYear(),
                request.getPaymentDueDate(),
                request.getNote());
    }

    @Transactional
    @Override
    public InvoiceResponse createEndOfMonthInvoice(InvoiceCreationRequest request) {
        Contract contract = getValidContract(request.getContractId());
        int month = getMonthOrDefault(request.getMonth());
        int year = getYearOrDefault(request.getYear());

        validateEndOfContractMonth(contract, month, year);
        ensureMainInvoiceExists(request.getContractId(), month, year);
        ensureEndInvoiceNotExists(request.getContractId(), month, year);

        LocalDate paymentDueDate = Optional.ofNullable(request.getPaymentDueDate())
                .orElse(LocalDate.of(year, month, 1).with(TemporalAdjusters.lastDayOfMonth()));
        validatePaymentDueDate(paymentDueDate, year, month);

        List<InvoiceItemResponse> items = buildElectricWaterCharges(contract.getRoom(), contract, month, year);
        Invoice invoice = buildGenericInvoice(
                contract, month, year, items, request.getNote(), paymentDueDate, InvoiceType.CUOI_CUNG);

        return invoiceMapper.toInvoiceResponse(invoiceRepository.save(invoice));
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
        if (invoice.getInvoiceStatus() == InvoiceStatus.CHUA_THANH_TOAN) {
            invoice.setInvoiceStatus(InvoiceStatus.DA_THANH_TOAN);
        } else if (invoice.getInvoiceStatus() == InvoiceStatus.HUY) {
            invoice.setInvoiceStatus(InvoiceStatus.DA_THANH_TOAN);
        } else {
            throw new AppException(ErrorCode.INVALID_INVOICE_STATUS);
        }

        invoice.setUpdatedAt(Instant.now());

        invoiceRepository.save(invoice);
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
    public PaginatedResponse<InvoiceResponse> getInvoicesForTenant(
            InvoiceFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        var user = userService.getCurrentUser();

        List<InvoiceStatus> statusList = List.of(
                InvoiceStatus.CHO_THANH_TOAN,
                InvoiceStatus.DA_THANH_TOAN,
                InvoiceStatus.QUA_HAN
        );

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

    @Transactional
    @Override
    public PaymentBatchResponse generateMonthlyPaymentRequests() {
        YearMonth current = YearMonth.now();

        List<Invoice> invoices = invoiceRepository.findAllByStatusAndMonth(
                InvoiceStatus.CHUA_THANH_TOAN,
                current.getMonthValue(),
                current.getYear()
        );

        if (invoices.isEmpty()) {
            throw new AppException(ErrorCode.NO_PENDING_INVOICES);
        }

        int created = 0;
        Set<String> notifiedEmails = new HashSet<>();

        for (Invoice invoice : invoices) {

            boolean exists = paymentReceiptRepository.existsByInvoiceId(invoice.getId());
            if (exists) {
                continue;
            }

            //Tạo phiếu thanh toán
            PaymentReceipt receipt = PaymentReceipt.builder()
                    .invoice(invoice)
                    .receiptCode(codeGeneratorService.generateReceiptCode())
                    .amount(invoice.getTotalAmount())
                    .paymentMethod(PaymentMethod.CHON_PHUONG_THUC)
                    .paymentStatus(PaymentStatus.CHO_THANH_TOAN)
                    .collectedBy(userService.getCurrentUser().getFullName())
                    .paymentDate(null)
                    .note("Phiếu thanh toán cho hóa đơn " + invoice.getInvoiceCode() + " đã được tạo")
                    .build();

            paymentReceiptRepository.save(receipt);
            created++;

            //Cập nhật trạng thái hóa đơn
            invoice.setInvoiceStatus(InvoiceStatus.CHO_THANH_TOAN);
            invoice.setUpdatedAt(Instant.now());
            invoiceRepository.save(invoice);

            //Gửi email tới tất cả khách thuê có tài khoản
            invoice.getContract().getTenants().forEach(tenant -> {
                User user = tenant.getUser();
                if (user != null && user.getEmail() != null) {
                    emailService.sendPaymentNotificationToTenant(
                            user.getEmail(),
                            user.getFullName(),
                            invoice,
                            receipt
                    );
                    notifiedEmails.add(user.getEmail());
                }
            });
        }

        if (created == 0) {
            throw new AppException(ErrorCode.ALL_INVOICES_ALREADY_HAVE_RECEIPTS);
        }

        return PaymentBatchResponse.builder()
                .totalInvoices(invoices.size())
                .createdReceipts(created)
                .notifiedUsers(notifiedEmails.size())
                .build();
    }

    @Transactional
    @Override
    public PaymentMethodResponse confirmPaymentMethod(String receiptId, PaymentMethodRequest request) {
        PaymentReceipt receipt = paymentReceiptRepository.findById(receiptId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_RECEIPT_NOT_FOUND));

        if (request.getPaymentMethod() == PaymentMethod.CHON_PHUONG_THUC) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_METHOD);
        }

        switch (request.getPaymentMethod()) {
            case TIEN_MAT -> {
                receipt.setPaymentMethod(request.getPaymentMethod());
                receipt.setPaymentStatus(PaymentStatus.CHO_XAC_NHAN);
                receipt.setUpdatedAt(Instant.now());
                paymentReceiptRepository.save(receipt);
                emailService.notifyOwnerForCashReceipt(receipt, invoiceMapper.getRepresentativeName(receipt.getInvoice()));
            }
            case CHUYEN_KHOAN, VNPAY, ZALOPAY, MOMO -> throw new AppException(ErrorCode.NOT_SUPPORTED_YET);

            default -> throw new AppException(ErrorCode.INVALID_PAYMENT_METHOD);
        }

        return PaymentMethodResponse.builder()
                .id(receipt.getId())
                .paymentMethod(receipt.getPaymentMethod())
                .build();
    }

    @Transactional
    @Override
    public RejectPaymentResponse rejectPaymentReceipt(String receiptId, RejectPaymentRequest request) {
        PaymentReceipt receipt = paymentReceiptRepository.findById(receiptId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_RECEIPT_NOT_FOUND));

        if (receipt.getPaymentStatus() != PaymentStatus.CHO_THANH_TOAN) {
            throw new AppException(ErrorCode.CANNOT_REFUSE_PAYMENTS);
        }

        receipt.setPaymentStatus(PaymentStatus.TU_CHOI);
        receipt.setNote(request.getReason());
        receipt.setUpdatedAt(Instant.now());
        paymentReceiptRepository.save(receipt);

        String representativeName = invoiceMapper.getRepresentativeName(receipt.getInvoice());
        emailService.notifyOwnerRejectedReceipt(receipt, representativeName);

        return RejectPaymentResponse.builder()
                .id(receipt.getId())
                .paymentStatus(receipt.getPaymentStatus())
                .build();
    }
    @Transactional
    @Override
    public void confirmCashPayment(String receiptId) {
        PaymentReceipt receipt = paymentReceiptRepository.findById(receiptId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_RECEIPT_NOT_FOUND));

        if (receipt.getPaymentMethod() != PaymentMethod.TIEN_MAT) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_METHOD);
        }

        if (receipt.getPaymentStatus() != PaymentStatus.CHO_XAC_NHAN) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_STATUS);
        }


        receipt.setPaymentStatus(PaymentStatus.DA_THANH_TOAN);
        receipt.setPaymentDate(LocalDateTime.now());
        receipt.setUpdatedAt(Instant.now());
        paymentReceiptRepository.save(receipt);

        Invoice invoice = receipt.getInvoice();
        invoice.setInvoiceStatus(InvoiceStatus.DA_THANH_TOAN);
        invoice.setUpdatedAt(Instant.now());
        invoiceRepository.save(invoice);

        emailService.notifyTenantPaymentConfirmed(receipt);
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

        LocalDate contractStart = contract.getStartDate().toLocalDate();
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

        int numberOfPeople = contract.getNumberOfPeople();
        int vehicleNumber =
                contract.getVehicles() != null ? contract.getVehicles().size() : 0;

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

    private BigDecimal calculateTotalAmount(List<InvoiceItemResponse> items) {
        return items.stream()
                .map(InvoiceItemResponse::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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

    private void validateContractForInvoice(Contract contract, int month, int year) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth());

        LocalDate contractStartDate = contract.getStartDate().toLocalDate();
        LocalDate contractEndDate = contract.getEndDate().toLocalDate();

        if (contractStartDate.isAfter(endOfMonth) || contractEndDate.isBefore(startOfMonth)) {
            throw new AppException(ErrorCode.CONTRACT_EXPIRED);
        }

        if (invoiceRepository.existsByContractIdAndMonthAndYear(contract.getId(), month, year)) {
            throw new AppException(ErrorCode.INVOICE_ALREADY_EXISTS);
        }
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

    private Contract getValidContract(String contractId) {
        return contractRepository
                .findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));
    }

    private int getMonthOrDefault(Integer month) {
        return Optional.ofNullable(month).orElse(LocalDate.now().getMonthValue());
    }

    private int getYearOrDefault(Integer year) {
        return Optional.ofNullable(year).orElse(LocalDate.now().getYear());
    }

    private LocalDate getPaymentDueDateOrDefault(LocalDate due, int month, int year) {
        return Optional.ofNullable(due).orElse(LocalDate.of(year, month, 5));
    }

    private void validateEndOfContractMonth(Contract contract, int month, int year) {
        LocalDate contractEnd = contract.getEndDate().toLocalDate();
        if (contractEnd.getMonthValue() != month || contractEnd.getYear() != year) {
            throw new AppException(ErrorCode.NOT_LAST_MONTH_OF_CONTRACT);
        }
    }

    private void ensureMainInvoiceExists(String contractId, int month, int year) {
        if (!invoiceRepository.existsByContractIdAndMonthAndYearAndInvoiceType(
                contractId, month, year, InvoiceType.HANG_THANG)) {
            throw new AppException(ErrorCode.MISSING_MAIN_INVOICE);
        }
    }

    private void ensureEndInvoiceNotExists(String contractId, int month, int year) {
        if (invoiceRepository.existsByContractIdAndMonthAndYearAndInvoiceType(
                contractId, month, year, InvoiceType.CUOI_CUNG)) {
            throw new AppException(ErrorCode.DUPLICATE_END_INVOICE);
        }
    }

    private String findServiceIdByItem(Map<String, ServiceRoom> serviceRoomMap, String serviceName) {
        return serviceRoomMap.values().stream()
                .filter(sr -> sr.getService().getName().equals(serviceName))
                .map(sr -> sr.getService().getId())
                .findFirst()
                .orElse(null);
    }

    private List<Contract> getValidContractsByBuilding(String buildingId, Integer month, Integer year) {
        return getValidContracts(month, year).stream()
                .filter(c -> c.getRoom().getFloor().getBuilding().getId().equals(buildingId))
                .toList();
    }

    private List<Contract> getValidContractsByFloor(String floorId, Integer month, Integer year) {
        return getValidContracts(month, year).stream()
                .filter(c -> c.getRoom().getFloor().getId().equals(floorId))
                .toList();
    }
}
