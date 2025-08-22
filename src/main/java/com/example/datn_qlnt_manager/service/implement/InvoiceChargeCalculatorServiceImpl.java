package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.*;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceItemResponse;
import com.example.datn_qlnt_manager.entity.*;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.repository.*;
import com.example.datn_qlnt_manager.service.InvoiceChargeCalculatorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceChargeCalculatorServiceImpl implements InvoiceChargeCalculatorService {

    ServiceRoomRepository serviceRoomRepository;
    MeterRepository meterRepository;
    MeterReadingRepository meterReadingRepository;

    @Override
    public List<InvoiceItemResponse> generateInvoiceItems(
            Contract contract,
            int month,
            int year,
            InvoiceType invoiceType,
            List<InvoiceItemResponse> additionalItems
    ) {
        List<InvoiceItemResponse> items = new ArrayList<>();

        boolean isFirstMonth = isFirstMonth(contract, month, year);

        if (invoiceType == InvoiceType.HANG_THANG) {
            InvoiceItemResponse roomCharge = calculateRoomPrice(contract, month, year);
            if (roomCharge != null) {
                items.add(roomCharge);
            }

            List<ServiceRoom> serviceRooms = serviceRoomRepository.findByRoomId(contract.getRoom().getId());

            for (ServiceRoom serviceRoom : serviceRooms) {
                Service service = serviceRoom.getService();
                ServiceCalculation calculation = service.getServiceCalculation();

                switch (calculation) {
                    case TINH_THEO_PHONG ->
                            items.add(calculateByRoomService(service, serviceRoom, contract, month, year));
                    case TINH_THEO_NGUOI ->
                            items.add(calculateByPeopleService(service, serviceRoom, contract, month, year));
                    case TINH_THEO_PHUONG_TIEN ->
                            items.add(calculateByVehicleService(service, serviceRoom, contract, month, year));
                    case TINH_THEO_SO -> {
                        if (isFirstMonth) {
                            continue; //bỏ qua dịch vụ tính theo số ở tháng đầu tiên
                        }
                        MeterReading reading = getPreviousMonthReading(service, contract, month, year);
                        if (reading == null) {
                            throw new AppException(ErrorCode.METER_READING_NOT_FOUND);
                        }
                        items.add(calculateByQuantityService(service, serviceRoom, contract, reading));
                    }
                    default ->
                            throw new AppException(ErrorCode.INVALID_SERVICE_CALCULATION);
                }
            }
        }

        // Hóa đơn cuối chỉ bao gồm dịch vụ tính theo số trong tháng hiện tại
        if (invoiceType == InvoiceType.CUOI_CUNG) {
            List<ServiceRoom> serviceRooms = serviceRoomRepository.findByRoomId(contract.getRoom().getId());
            for (ServiceRoom serviceRoom : serviceRooms) {
                Service service = serviceRoom.getService();
                if (service.getServiceCalculation() != ServiceCalculation.TINH_THEO_SO) continue;

                Meter meter = meterRepository.findByRoomIdAndServiceId(
                        contract.getRoom().getId(), service.getId()
                ).orElseThrow(() -> new AppException(ErrorCode.METER_NOT_FOUND));

                MeterReading reading = meterReadingRepository.findByMeterIdAndMonthAndYear(
                        meter.getId(), month, year
                ).orElseThrow(() -> new AppException(ErrorCode.METER_READING_NOT_FOUND));

                items.add(calculateByQuantityService(service, serviceRoom, contract, reading));
            }
        }

        // Thêm dòng bổ sung nếu có (ví dụ: đền bù)
        if (additionalItems != null && !additionalItems.isEmpty()) {
            items.addAll(additionalItems);
        }

        return items;
    }

    // Lấy chỉ số tháng trước
    private MeterReading getPreviousMonthReading(Service service, Contract contract, int month, int year) {
        Meter meter = meterRepository.findByRoomIdAndServiceId(contract.getRoom().getId(), service.getId())
                .orElseThrow(() -> new AppException(ErrorCode.METER_NOT_FOUND));

        YearMonth previousMonth = YearMonth.of(year, month).minusMonths(1);

        return meterReadingRepository.findByMeterIdAndMonthAndYear(
                meter.getId(),
                previousMonth.getMonthValue(),
                previousMonth.getYear()
        ).orElseThrow(() -> new AppException(ErrorCode.METER_READING_NOT_FOUND));
    }

    // Kiểm tra xem có phải là tháng đầu của hợp đồng không
    private boolean isFirstMonth(Contract contract, int month, int year) {
        YearMonth contractStart = YearMonth.from(contract.getStartDate());
        YearMonth current = YearMonth.of(year, month);
        return contractStart.equals(current);
    }

    private boolean isLastMonth(Contract contract, int month, int year) {
        if (contract.getEndDate() == null) {
            return false; // chưa có ngày kết thúc => không phải tháng cuối
        }
        YearMonth contractEnd = YearMonth.from(contract.getEndDate());
        YearMonth current = YearMonth.of(year, month);
        return contractEnd.equals(current);
    }

    // tính dịch vụ theo phòng
    private InvoiceItemResponse calculateByRoomService(
            Service service,
            ServiceRoom serviceRoom,
            Contract contract,
            int month,
            int year
    ) {
        // Lấy đơn giá từ ServiceRoom
        BigDecimal initialUnitPrice = serviceRoom.getUnitPrice();
        if (initialUnitPrice == null || initialUnitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.UNIT_PRICE_REQUIRED);
        }

        BigDecimal proportion = calculateProportion(contract, month, year);
        BigDecimal actualUnitPrice = initialUnitPrice.multiply(proportion).setScale(0, RoundingMode.HALF_UP);

        return InvoiceItemResponse.builder()
                .serviceRoomId(serviceRoom.getId())
                .serviceName(service.getName())
                .serviceCategory(service.getServiceCategory())
                .serviceCalculation(ServiceCalculation.TINH_THEO_PHONG)
                .quantity(1)
                .unit(service.getUnit())
                .unitPrice(actualUnitPrice)
                .amount(actualUnitPrice)
                .description(buildDescription(contract, month, year, service.getName()))
                .build();
    }

    // tính dịch vụ theo phương tiện
    private InvoiceItemResponse calculateByVehicleService(
            Service service,
            ServiceRoom serviceRoom,
            Contract contract,
            int month,
            int year
    ) {
        int vehicleCount = (contract.getContractVehicles() != null) ? contract.getContractVehicles().size() : 0;
        return calculateByCountService(
                service,
                serviceRoom,
                contract,
                month,
                year,
                vehicleCount,
                ErrorCode.INVALID_VEHICLE_COUNT,
                ServiceCalculation.TINH_THEO_PHUONG_TIEN
        );
    }

    //tính dịch vụ theo người
    private InvoiceItemResponse calculateByPeopleService(
            Service service,
            ServiceRoom serviceRoom,
            Contract contract,
            int month,
            int year
    ) {
        int peopleCount = (contract.getContractTenants() != null) ? contract.getContractTenants().size() : 0;
        return calculateByCountService(
                service,
                serviceRoom,
                contract,
                month,
                year,
                peopleCount,
                ErrorCode.INVALID_NUMBER_OF_PEOPLE,
                ServiceCalculation.TINH_THEO_NGUOI
        );
    }

    //dịch vụ tính theo số
    private InvoiceItemResponse calculateByQuantityService(
            Service service,
            ServiceRoom serviceRoom,
            Contract contract,
            MeterReading reading
    ) {
        if (reading == null) {
            throw new AppException(ErrorCode.METER_READING_NOT_FOUND);
        }

        //Lấy số lượng tiêu thụ
        Integer quantity = reading.getQuantity();
        if (quantity == null) {
            quantity = reading.getNewIndex() - reading.getOldIndex();
        }

        BigDecimal unitPrice;

        if (service.getServiceCategory() == ServiceCategory.DIEN) {
            unitPrice = contract.getElectricPrice() != null ? contract.getElectricPrice() : serviceRoom.getUnitPrice();
        } else if (service.getServiceCategory() == ServiceCategory.NUOC) {
            unitPrice = contract.getWaterPrice() != null ? contract.getWaterPrice() : serviceRoom.getUnitPrice();
        } else {
            throw new AppException(ErrorCode.INVALID_SERVICE_CATEGORY);
        }

        // tính thành tiền
        BigDecimal amount = unitPrice.multiply(BigDecimal.valueOf(quantity));

        return InvoiceItemResponse.builder()
                .serviceRoomId(serviceRoom.getId())
                .serviceName(service.getName())
                .serviceCategory(service.getServiceCategory())
                .serviceCalculation(ServiceCalculation.TINH_THEO_SO)
                .oldIndex(reading.getOldIndex())
                .newIndex(reading.getNewIndex())
                .quantity(quantity)
                .unit(service.getUnit())
                .unitPrice(unitPrice)
                .amount(amount)
                .description("Tiền " + service.getName() + " phòng " + contract.getRoom().getRoomCode())
        .build();

    }

    // tính tiền phòng
    private InvoiceItemResponse calculateRoomPrice(Contract contract, int month, int year) {
        BigDecimal roomPrice = contract.getRoomPrice();
        if (roomPrice == null || roomPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        BigDecimal proportion = calculateProportion(contract, month, year);
        BigDecimal unitPrice = roomPrice.multiply(proportion).setScale(0, RoundingMode.HALF_UP);

        String description;
        if (isFirstMonth(contract, month, year)) {
            description = "Tiền phòng tháng đầu tính theo số ngày thực tế thuê.";
        } else if (isLastMonth(contract, month, year)) {
            description = "Tiền phòng tháng cuối tính theo số ngày thực tế thuê.";
        } else {
            description = "Tiền phòng " + contract.getRoom().getRoomCode() + " tháng " + month + "/" + year
                    + " phòng " + contract.getRoom().getRoomCode();
        }

        return InvoiceItemResponse.builder()
                .serviceRoomId(null)
                .serviceName("Tiền phòng")
                .serviceCategory(ServiceCategory.TIEN_PHONG)
                .serviceCalculation(ServiceCalculation.TINH_THEO_PHONG)
                .quantity(1)
                .unit("Phòng")
                .unitPrice(unitPrice)
                .amount(unitPrice)
                .description(description)
                .build();
    }

    // build dùng chung cho dịch vụ tính theo số lượng (người/xe)
    private InvoiceItemResponse calculateByCountService(
            Service service,
            ServiceRoom serviceRoom,
            Contract contract,
            int month,
            int year,
            int quantity,
            ErrorCode emptyErrorCode,
            ServiceCalculation calculation
    ) {
        if (quantity <= 0) {
            throw new AppException(emptyErrorCode);
        }

        BigDecimal initialUnitPrice = serviceRoom.getUnitPrice();
        if (initialUnitPrice == null) {
            throw new AppException(ErrorCode.UNIT_PRICE_REQUIRED);
        }

        BigDecimal proportion = calculateProportion(contract, month, year);
        BigDecimal actualUnitPrice = initialUnitPrice.multiply(proportion).setScale(0, RoundingMode.HALF_UP);

        BigDecimal amount = actualUnitPrice.multiply(BigDecimal.valueOf(quantity));

        return InvoiceItemResponse.builder()
                .serviceRoomId(serviceRoom.getId())
                .serviceName(service.getName())
                .serviceCategory(service.getServiceCategory())
                .serviceCalculation(calculation)
                .quantity(quantity)
                .unit(service.getUnit())
                .unitPrice(actualUnitPrice)
                .amount(amount)
                .description(buildDescription(contract, month, year, service.getName()))
                .build();
    }


    private String buildDescription(Contract contract, int month, int year, String serviceName) {
        if (isFirstMonth(contract, month, year)) {
            return "Tiền " + serviceName + " tháng đầu tính theo số ngày thực tế thuê.";
        } else if (isLastMonth(contract, month, year)) {
            return "Tiền " + serviceName + " tháng cuối tính theo số ngày thực tế thuê.";
        } else {
            return "Tiền " + serviceName + " phòng " + contract.getRoom().getRoomCode();
        }
    }

    private BigDecimal calculateProportion(Contract contract, int month, int year) {
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());

        LocalDate contractStartDate = contract.getStartDate();
        LocalDate contractEndDate = contract.getEndDate() != null ? contract.getEndDate() : null;

        // Nếu hợp đồng bao phủ toàn bộ tháng thì không cần tính tỷ lệ
        if ((contractStartDate.isBefore(firstDayOfMonth) || contractStartDate.isEqual(firstDayOfMonth)) &&
                (contractEndDate == null || contractEndDate.isAfter(lastDayOfMonth) || contractEndDate.isEqual(lastDayOfMonth))) {
            return BigDecimal.ONE; // full tháng
        }

        // Tính số ngày thuê thực tế
        LocalDate from = contractStartDate.isAfter(firstDayOfMonth) ? contractStartDate : firstDayOfMonth;
        LocalDate to = contractEndDate != null && contractEndDate.isBefore(lastDayOfMonth) ? contractEndDate : lastDayOfMonth;

        int daysInMonth = (int) ChronoUnit.DAYS.between(firstDayOfMonth, lastDayOfMonth.plusDays(1));
        int rentedDays = (int) ChronoUnit.DAYS.between(from, to.plusDays(1));

        return BigDecimal.valueOf(rentedDays)
                .divide(BigDecimal.valueOf(daysInMonth), 4, RoundingMode.HALF_UP);
    }
}