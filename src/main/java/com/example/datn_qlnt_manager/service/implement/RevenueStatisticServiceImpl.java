package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.InvoiceStatus;
import com.example.datn_qlnt_manager.dto.statistics.revenue.DamageOverdueResponse;
import com.example.datn_qlnt_manager.dto.statistics.revenue.InvoiceRevenueResponse;
import com.example.datn_qlnt_manager.dto.statistics.revenue.RevenueStatisticRequest;
import com.example.datn_qlnt_manager.dto.statistics.revenue.RevenueStatisticResponse;
import com.example.datn_qlnt_manager.entity.Building;
import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.repository.BuildingRepository;
import com.example.datn_qlnt_manager.repository.DepositRepository;
import com.example.datn_qlnt_manager.repository.InvoiceDetailsRepository;
import com.example.datn_qlnt_manager.repository.InvoiceRepository;
import com.example.datn_qlnt_manager.service.RevenueStatisticService;
import com.example.datn_qlnt_manager.service.UserService;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RevenueStatisticServiceImpl implements RevenueStatisticService {
    InvoiceRepository invoiceRepository;
    InvoiceDetailsRepository invoiceDetailsRepository;
    DepositRepository depositRepository;
    BuildingRepository buildingRepository;
    UserService userService;

    @Override
    public RevenueStatisticResponse getRevenueStatistic(RevenueStatisticRequest request) {

        User user = userService.getCurrentUser();

        int month = Optional.ofNullable(request.getMonth()).orElse(LocalDate.now().getMonthValue());
        int year = Optional.ofNullable(request.getYear()).orElse(LocalDate.now().getYear());

        Building building = null;
        if (request.getBuildingId() != null) {
            building = buildingRepository.findById(request.getBuildingId())
                    .orElseThrow(() -> new AppException(ErrorCode.BUILDING_NOT_FOUND));
        }

        InvoiceRevenueResponse invoiceRevenue = invoiceDetailsRepository.getInvoiceRevenueStatistics(
                List.of(
                        InvoiceStatus.CHUA_THANH_TOAN,
                        InvoiceStatus.CHO_THANH_TOAN,
                        InvoiceStatus.DA_THANH_TOAN
                ),
                user.getId(),
                month,
                year,
                Optional.ofNullable(building).map(Building::getId).orElse(null),
                BigDecimal.ZERO);

        DamageOverdueResponse damageOverdue = invoiceRepository.getDamageAndOverdueAmount(
                user.getId(),
                month,
                year,
                Optional.ofNullable(building).map(Building::getId).orElse(null),
                BigDecimal.ZERO
        );

        BigDecimal unreturnedDeposit = depositRepository.getTotalUnreturnedDeposits(
                user.getId(),
                month,
                year,
                Optional.ofNullable(building).map(Building::getId).orElse(null)
        );

        return RevenueStatisticResponse.builder()
                .buildingId(Optional.ofNullable(building).map(Building::getId).orElse(null))
                .buildingName(Optional.ofNullable(building).map(Building::getBuildingName).orElse(null))
                .year(year)
                .month(month)
                .expectedRevenue(invoiceRevenue.getExpectedRevenue())
                .currentRevenue(invoiceRevenue.getCurrentRevenue())
                .paidRoomFee(invoiceRevenue.getPaidRoomFee())
                .paidEnergyFee(invoiceRevenue.getPaidEnergyFee())
                .paidWaterFee(invoiceRevenue.getPaidWaterFee())
                .paidServiceFee(invoiceRevenue.getPaidServiceFee())
                .compensationAmount(invoiceRevenue.getCompensationAmount())
                .damageAmount(damageOverdue.getDamageAmount())
                .overdueAmount(damageOverdue.getOverdueAmount())
                .unreturnedDeposit(unreturnedDeposit)
                .build();
    }

}