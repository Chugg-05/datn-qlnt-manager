package com.example.datn_qlnt_manager.service.implement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.example.datn_qlnt_manager.common.ContractStatus;
import com.example.datn_qlnt_manager.common.ServiceCalculation;
import com.example.datn_qlnt_manager.common.ServiceCategory;
import com.example.datn_qlnt_manager.common.ServiceRoomStatus;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceItemResponse;
import com.example.datn_qlnt_manager.entity.*;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.repository.ServiceRoomRepository;
import com.example.datn_qlnt_manager.service.InvoiceChargeCalculatorService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceChargeCalculatorServiceImpl implements InvoiceChargeCalculatorService {

    ServiceRoomRepository serviceRoomRepository;

    public List<InvoiceItemResponse> generateInvoiceItems(Contract contract, int month, int year) {
        List<InvoiceItemResponse> invoiceItems = new ArrayList<>();

        // 1. Tiền phòng
        InvoiceItemResponse roomCharge = calculateRoomPrice(contract, month, year);
        if (roomCharge != null) {
            invoiceItems.add(roomCharge);
        }

        // 2. Lấy các dịch vụ đang sử dụng
        List<ServiceRoom> serviceRooms = serviceRoomRepository.findByRoomIdAndServiceRoomStatus(
                contract.getRoom().getId(), ServiceRoomStatus.DANG_SU_DUNG);

        for (ServiceRoom sr : serviceRooms) {
            Service service = sr.getService();
            InvoiceItemResponse item = null;

            switch (service.getServiceCalculation()) {
                case TINH_THEO_NGUOI:
                    item = calculateByPeopleService(service, sr, contract, month, year);
                    break;

                case TINH_THEO_PHUONG_TIEN:
                    item = calculateByVehicleService(service, sr, contract, month, year);
                    break;

                case TINH_THEO_SO:
                    //                    Meter meter = meterService.findByServiceRoom(sr);
                    //                    MeterReading reading = meterReadingService.findByMeterAndMonthAndYear(meter,
                    // month, year);
                    //                        item = calculateByQuantityService(service, sr, contract, meter, reading);
                    break;

                case TINH_THEO_PHONG:
                    item = calculateByRoomService(service, sr, contract, month, year);
                    break;

                default:
                    // Có thể log hoặc bỏ qua các loại chưa hỗ trợ
                    continue;
            }

            if (item != null) {
                invoiceItems.add(item);
            }
        }

        return invoiceItems;
    }

    // tính tiền cọc
    public InvoiceItemResponse handleDeposit(
            Contract contract, BigDecimal deductionAmount, ContractStatus contractStatus) {
        BigDecimal deposit = contract.getDeposit();
        if (deposit == null || deposit.compareTo(BigDecimal.ZERO) <= 0) {
            return null; // Không có tiền cọc => không tạo dòng hóa đơn
        }

        BigDecimal actualRefundAmount;
        String description;

        switch (contractStatus) {
            case KET_THUC_DUNG_HAN:
                actualRefundAmount = deposit.negate(); // trả lại toàn bộ (âm thể hiện tiền trả lại)
                description = "Hoàn lại tiền cọc khi kết thúc hợp đồng đúng hạn";
                break;

            case KET_THUC_CO_BAO_TRUOC:
                if (deductionAmount == null || deductionAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    actualRefundAmount = deposit.negate();
                    description = "Hoàn lại tiền cọc khi kết thúc hợp đồng có báo trước";
                } else {
                    actualRefundAmount = deposit.subtract(deductionAmount).negate(); // trừ phần đền bù
                    description = "Hoàn lại tiền cọc sau khi trừ các khoản khấu trừ";
                }
                break;

            case TU_Y_HUY_BO:
                actualRefundAmount = BigDecimal.ZERO; // không hoàn cọc
                description = "Không hoàn lại tiền cọc do chấm dứt hợp đồng không báo trước";
                break;

            default:
                return null; // các trạng thái không áp dụng
        }

        if (actualRefundAmount.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        return InvoiceItemResponse.builder()
                .serviceName("Tiền cọc")
                .serviceCategory(ServiceCategory.TIEN_COC)
                .serviceCalculation(ServiceCalculation.TINH_THEO_PHONG)
                .quantity(1)
                .unit("VNĐ")
                .unitPrice(deposit)
                .amount(actualRefundAmount)
                .description(description)
                .build();
    }

    // tính dịch vụ theo phòng
    public InvoiceItemResponse calculateByRoomService(
            Service service, ServiceRoom serviceRoom, Contract contract, int month, int year) {
        // Lấy đơn giá từ ServiceRoom
        BigDecimal unitPrice = serviceRoom.getUnitPrice();
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.UNIT_PRICE_REQUIRED);
        }

        BigDecimal proportion = calculateProportion(contract, month, year);
        BigDecimal amount = unitPrice.multiply(proportion).setScale(0, RoundingMode.HALF_UP);

        return InvoiceItemResponse.builder()
                .serviceName(service.getName())
                .serviceCategory(service.getServiceCategory())
                .serviceCalculation(ServiceCalculation.TINH_THEO_PHONG)
                .quantity(1)
                .unit(service.getUnit())
                .unitPrice(unitPrice)
                .amount(amount)
                .description("")
                .build();
    }

    // tính dịch vụ theo phương tiện
    public InvoiceItemResponse calculateByVehicleService(
            Service service, ServiceRoom serviceRoom, Contract contract, int month, int year) {
        Set<Vehicle> vehicles = contract.getVehicles();
        int vehicleCount = (vehicles != null) ? vehicles.size() : 0;

        if (vehicleCount <= 0) {
            throw new AppException(ErrorCode.INVALID_VEHICLE_COUNT);
        }

        BigDecimal unitPrice = serviceRoom.getUnitPrice();
        if (unitPrice == null) {
            throw new AppException(ErrorCode.UNIT_PRICE_REQUIRED);
        }

        BigDecimal proportion = calculateProportion(contract, month, year);
        BigDecimal amount = unitPrice
                .multiply(BigDecimal.valueOf(vehicleCount))
                .multiply(proportion)
                .setScale(0, RoundingMode.HALF_UP);

        return InvoiceItemResponse.builder()
                .serviceName(service.getName())
                .serviceCategory(service.getServiceCategory())
                .serviceCalculation(ServiceCalculation.TINH_THEO_PHUONG_TIEN)
                .quantity(vehicleCount)
                .unit(service.getUnit())
                .unitPrice(unitPrice)
                .amount(amount)
                .description("Tiền " + service.getName() + " phòng "
                        + contract.getRoom().getRoomCode())
                .build();
    }

    // tính dịch vụ theo người
    public InvoiceItemResponse calculateByPeopleService(
            Service service, ServiceRoom serviceRoom, Contract contract, int month, int year) {
        // Kiểm tra số người hợp lệ
        Integer numberOfPeople = contract.getNumberOfPeople();
        if (numberOfPeople == null || numberOfPeople <= 0) {
            throw new AppException(ErrorCode.INVALID_NUMBER_OF_PEOPLE);
        }

        // Lấy đơn giá từ serviceRoom
        BigDecimal unitPrice = serviceRoom.getUnitPrice();
        if (unitPrice == null) {
            throw new AppException(ErrorCode.UNIT_PRICE_REQUIRED);
        }

        // Tính thành tiền
        BigDecimal proportion = calculateProportion(contract, month, year);
        BigDecimal amount = unitPrice
                .multiply(BigDecimal.valueOf(numberOfPeople))
                .multiply(proportion)
                .setScale(0, RoundingMode.HALF_UP);

        return InvoiceItemResponse.builder()
                .serviceName(service.getName())
                .serviceCategory(service.getServiceCategory())
                .serviceCalculation(ServiceCalculation.TINH_THEO_NGUOI)
                .quantity(numberOfPeople)
                .unit(service.getUnit())
                .unitPrice(unitPrice)
                .amount(amount)
                .description("Tiền " + service.getName() + " phòng "
                        + contract.getRoom().getRoomCode())
                .build();
    }

    // dịch vụ tính theo số
    public InvoiceItemResponse calculateByQuantityService(
            Service service, ServiceRoom serviceRoom, Contract contract, MeterReading reading) {
        if (reading == null) {
            throw new AppException(ErrorCode.METER_READING_NOT_FOUND);
        }

        // Lấy số lượng tiêu thụ
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
                .serviceName(service.getName())
                .serviceCategory(service.getServiceCategory())
                .serviceCalculation(ServiceCalculation.TINH_THEO_SO)
                .oldIndex(reading.getOldIndex())
                .newIndex(reading.getNewIndex())
                .quantity(quantity)
                .unit(service.getUnit())
                .unitPrice(unitPrice)
                .amount(amount)
                .description("Tiền " + service.getName() + " phòng "
                        + contract.getRoom().getRoomCode())
                .build();
    }

    // tính tiền phòng
    public InvoiceItemResponse calculateRoomPrice(Contract contract, int month, int year) {
        BigDecimal roomPrice = contract.getRoomPrice();
        if (roomPrice == null || roomPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        BigDecimal proportion = calculateProportion(contract, month, year);
        BigDecimal amount = roomPrice.multiply(proportion).setScale(0, RoundingMode.HALF_UP);

        return InvoiceItemResponse.builder()
                .serviceName("Tiền phòng")
                .serviceCategory(ServiceCategory.TIEN_PHONG)
                .serviceCalculation(ServiceCalculation.TINH_THEO_PHONG)
                .quantity(1)
                .unit("Phòng")
                .unitPrice(roomPrice)
                .amount(amount)
                .description("Tiền phòng " + contract.getRoom().getRoomCode())
                .build();
    }

    private BigDecimal calculateProportion(Contract contract, int month, int year) {
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());

        LocalDate contractStartDate = contract.getStartDate().toLocalDate();
        LocalDate contractEndDate =
                contract.getEndDate() != null ? contract.getEndDate().toLocalDate() : null;

        // Nếu hợp đồng bao phủ toàn bộ tháng thì không cần tính tỷ lệ
        if ((contractStartDate.isBefore(firstDayOfMonth) || contractStartDate.isEqual(firstDayOfMonth))
                && (contractEndDate == null
                        || contractEndDate.isAfter(lastDayOfMonth)
                        || contractEndDate.isEqual(lastDayOfMonth))) {
            return BigDecimal.ONE; // full tháng
        }

        // Tính số ngày thuê thực tế
        LocalDate from = contractStartDate.isAfter(firstDayOfMonth) ? contractStartDate : firstDayOfMonth;
        LocalDate to =
                contractEndDate != null && contractEndDate.isBefore(lastDayOfMonth) ? contractEndDate : lastDayOfMonth;

        int daysInMonth = (int) ChronoUnit.DAYS.between(firstDayOfMonth, lastDayOfMonth.plusDays(1));
        int rentedDays = (int) ChronoUnit.DAYS.between(from, to.plusDays(1));

        return BigDecimal.valueOf(rentedDays).divide(BigDecimal.valueOf(daysInMonth), 4, RoundingMode.HALF_UP);
    }
}
