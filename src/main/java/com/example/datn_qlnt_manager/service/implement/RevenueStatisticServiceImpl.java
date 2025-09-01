package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.InvoiceStatus;
import com.example.datn_qlnt_manager.common.RevenueCategory;
import com.example.datn_qlnt_manager.dto.statistics.revenue.request.RevenueStatisticRequest;
import com.example.datn_qlnt_manager.dto.statistics.revenue.response.RevenueComparisonResponse;
import com.example.datn_qlnt_manager.entity.Building;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.repository.BuildingRepository;
import com.example.datn_qlnt_manager.repository.DepositRepository;
import com.example.datn_qlnt_manager.repository.InvoiceDetailsRepository;
import com.example.datn_qlnt_manager.service.RevenueStatisticService;
import com.example.datn_qlnt_manager.service.UserService;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RevenueStatisticServiceImpl implements RevenueStatisticService {
    InvoiceDetailsRepository invoiceDetailsRepository;
    DepositRepository depositRepository;
    BuildingRepository buildingRepository;
    UserService userService;

    @Override
    public List<RevenueComparisonResponse> compareRevenueByBuilding(RevenueStatisticRequest request) {
        List<RevenueComparisonResponse> results = new ArrayList<>();

        String userId = userService.getCurrentUser().getId();

        int month = Optional.ofNullable(request.getMonth()).orElse(LocalDate.now().getMonthValue());
        int year = Optional.ofNullable(request.getYear()).orElse(LocalDate.now().getYear());

        Building building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new AppException(ErrorCode.BUILDING_NOT_FOUND));

        String buildingId = building.getId();

        // Tính tháng trước
        int prevMonth = (month == 1) ? 12 : month - 1;
        int prevYear = (month == 1) ? year - 1 : year;

        //Doanh thu dự kiến
        results.add(buildResponse(
                RevenueCategory.EXPECTED_REVENUE,

                invoiceDetailsRepository.getExpectedRevenue(
                        userId,
                        List.of(InvoiceStatus.CHUA_THANH_TOAN,
                                InvoiceStatus.CHO_THANH_TOAN,
                                InvoiceStatus.DA_THANH_TOAN),
                        month, year, buildingId),

                invoiceDetailsRepository.getExpectedRevenue(
                        userId,
                        List.of(InvoiceStatus.CHUA_THANH_TOAN,
                                InvoiceStatus.CHO_THANH_TOAN,
                                InvoiceStatus.DA_THANH_TOAN),
                        prevMonth, prevYear, buildingId)
        ));

        //Doanh thu thực tế
        results.add(buildResponse(
                RevenueCategory.ACTUAL_REVENUE,
                invoiceDetailsRepository.getCurrentRevenue(userId, month, year, buildingId),
                invoiceDetailsRepository.getCurrentRevenue(userId, prevMonth, prevYear, buildingId)
        ));

        //Tiền phòng
        results.add(buildResponse(
                RevenueCategory.ROOM,
                invoiceDetailsRepository.getRoomRevenue(userId, month, year, buildingId),
                invoiceDetailsRepository.getRoomRevenue(userId, prevMonth, prevYear, buildingId)
        ));

        //Tiền điện
        results.add(buildResponse(
                RevenueCategory.ENERGY,
                invoiceDetailsRepository.getElectricRevenue(userId, month, year, buildingId),
                invoiceDetailsRepository.getElectricRevenue(userId, prevMonth, prevYear, buildingId)
        ));

        //Tiền nước
        results.add(buildResponse(
                RevenueCategory.WATER,
                invoiceDetailsRepository.getWaterRevenue(userId, month, year, buildingId),
                invoiceDetailsRepository.getWaterRevenue(userId, prevMonth, prevYear, buildingId)
        ));

        //Tiền dịch vụ
        results.add(buildResponse(
                RevenueCategory.SERVICE,
                invoiceDetailsRepository.getServiceRevenue(userId, month, year, buildingId),
                invoiceDetailsRepository.getServiceRevenue(userId, prevMonth, prevYear, buildingId)
        ));

        //Doanh thu quá hạn
        results.add(buildResponse(
                RevenueCategory.OVERDUE,
                invoiceDetailsRepository.getOverdueRevenue(userId, month, year, buildingId),
                invoiceDetailsRepository.getOverdueRevenue(userId, prevMonth, prevYear, buildingId)
        ));

        //Không thể thanh toán
        results.add(buildResponse(
                RevenueCategory.DAMAGE,
                invoiceDetailsRepository.getUncollectibleRevenue(userId, month, year, buildingId),
                invoiceDetailsRepository.getUncollectibleRevenue(userId, prevMonth, prevYear, buildingId)
        ));

        //Tiền cọc giữ lại
        results.add(buildResponse(
                RevenueCategory.DEPOSIT,
                depositRepository.getDepositRevenue(userId, month, year, buildingId),
                depositRepository.getDepositRevenue(userId, prevMonth, prevYear, buildingId)
        ));

        return results;
    }

    private RevenueComparisonResponse buildResponse(
            RevenueCategory category,
            BigDecimal current,
            BigDecimal previous
    ) {
        if (current == null) current = BigDecimal.ZERO;
        if (previous == null) previous = BigDecimal.ZERO;

        BigDecimal difference = current.subtract(previous);

        BigDecimal percent;
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            percent = current.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : BigDecimal.valueOf(100);
        } else {
            percent = difference.multiply(BigDecimal.valueOf(100))
                    .divide(previous, 2, RoundingMode.HALF_UP);
        }

        return RevenueComparisonResponse.builder()
                .category(category)
                .current(current)
                .previous(previous)
                .difference(difference)
                .percent(percent)
                .build();

    }


}