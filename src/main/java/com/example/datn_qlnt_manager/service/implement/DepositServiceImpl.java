package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.ContractStatus;
import com.example.datn_qlnt_manager.common.DepositStatus;
import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.DepositFilter;
import com.example.datn_qlnt_manager.dto.projection.DepositDetailView;
import com.example.datn_qlnt_manager.dto.response.deposit.ConfirmDepositResponse;
import com.example.datn_qlnt_manager.entity.Contract;
import com.example.datn_qlnt_manager.entity.ContractTenant;
import com.example.datn_qlnt_manager.entity.Deposit;
import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.repository.ContractTenantRepository;
import com.example.datn_qlnt_manager.repository.DepositRepository;
import com.example.datn_qlnt_manager.service.DepositService;
import com.example.datn_qlnt_manager.service.EmailService;
import com.example.datn_qlnt_manager.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DepositServiceImpl implements DepositService {
    DepositRepository depositRepository;
    ContractTenantRepository contractTenantRepository;
    UserService userService;
    EmailService emailService;

    @Override
    public PaginatedResponse<DepositDetailView> getDepositsByUserId(
            DepositFilter filter,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        User user = userService.getCurrentUser();

        Page<DepositDetailView> paging = depositRepository.findAllDepositByUserId(
                user.getId(),
                filter.getQuery(),
                filter.getBuilding(),
                filter.getFloor(),
                filter.getRoom(),
                filter.getDepositStatus(),
                pageable);

        List<DepositDetailView> deposits = paging.getContent();

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(paging.getNumberOfElements())
                        .perPage(size)
                        .currentPage(page)
                        .totalPages(paging.getTotalPages())
                        .total(paging.getTotalElements())
                        .build())
                .build();

        return PaginatedResponse.<DepositDetailView>builder()
                .data(deposits)
                .meta(meta)
                .build();
    }

    @Transactional
    @Override
    public void createDepositForContract(Contract contract) {
        if (depositRepository.existsByContractId(contract.getId())) {
            throw new AppException(ErrorCode.DEPOSIT_ALREADY_EXISTS);
        }

        User user = contract.getRoom().getFloor().getBuilding().getUser();
        ContractTenant representative = contractTenantRepository
                .findByContractIdAndRepresentativeTrue(contract.getId())
                .orElseThrow(() -> new AppException(ErrorCode.REPRESENTATIVE_NOT_FOUND));

        Deposit deposit = Deposit.builder()
                .contract(contract)
                .depositor(representative.getTenant().getFullName())
                .depositRecipient(user.getFullName())
                .depositAmount(contract.getDeposit())
                .depositStatus(DepositStatus.DA_DAT_COC)
                .refundAmount(contract.getDeposit())
                .depositDate(LocalDateTime.now())
                .depositRefundDate(null)
                .securityDepositReturnDate(null)
                .depositHoldDate(LocalDate.now())
                .note("Tiền cọc cho hợp đồng thuê phòng " + contract.getRoom().getRoomCode())
                .build();

        deposit.setCreatedAt(Instant.now());
        deposit.setUpdatedAt(Instant.now());

        depositRepository.save(deposit);
    }

    @Transactional
    @Override
    public ConfirmDepositResponse confirmDepositRefund(String depositId) {
        Deposit deposit = getDepositOrThrow(depositId);

        Contract contract = deposit.getContract();

        if (contract.getStatus() != ContractStatus.KET_THUC_DUNG_HAN
        && contract.getStatus() != ContractStatus.KET_THUC_CO_BAO_TRUOC) {
            throw new AppException(ErrorCode.CONTRACT_NOT_ALLOW_CONFIRM_DEPOSIT);
        }

        BigDecimal refundAmount;
        BigDecimal depositAmount = deposit.getDepositAmount();
        String note;

        if (contract.getStatus() == ContractStatus.KET_THUC_DUNG_HAN) {
            refundAmount = depositAmount;
            note = "Trả đủ tiền cọc do kết thúc hợp đồng đúng hạn";
        } else {
            LocalDate now = LocalDate.now();

            long monthsRented = ChronoUnit.MONTHS.between(contract.getStartDate(), now);
            long totalMonths = ChronoUnit.MONTHS.between(contract.getStartDate(), contract.getOriginalEndDate());

            if (totalMonths <= 0) {
                throw new AppException(ErrorCode.INVALID_CONTRACT_DURATION);
            }

            BigDecimal monthlyDeposit = depositAmount.divide(BigDecimal.valueOf(totalMonths), RoundingMode.HALF_UP);
            refundAmount = monthlyDeposit.multiply(BigDecimal.valueOf(monthsRented));

            note = String.format(
                    "Trừ 1 phần tiền cọc do kết thúc hợp đồng trước thời hạn (đã thuê %d/%d tháng)",
                    monthsRented, totalMonths
            );
        }

        deposit.setDepositStatus(DepositStatus.CHO_XAC_NHAN);
        deposit.setDepositRefundDate(LocalDateTime.now());
        deposit.setRefundAmount(refundAmount);
        deposit.setNote(note);
        deposit.setUpdatedAt(Instant.now());

        deposit = depositRepository.save(deposit);
        emailService.notifyTenantDepositRefund(deposit);

        return toConfirmDepositResponse(deposit);
    }

    @Transactional
    @Override
    public ConfirmDepositResponse confirmReceipt(String depositId) {
        Deposit deposit = getDepositOrThrow(depositId);

        if (deposit.getDepositStatus() != DepositStatus.CHO_XAC_NHAN
                && deposit.getDepositStatus() != DepositStatus.CHUA_NHAN_COC
        ) {
            throw new AppException(ErrorCode.CONTRACT_NOT_ALLOW_CONFIRM_DEPOSIT);
        }

        deposit.setDepositStatus(DepositStatus.DA_HOAN_TRA);
        deposit.setSecurityDepositReturnDate(LocalDateTime.now());
        deposit.setUpdatedAt(Instant.now());

        deposit = depositRepository.save(deposit);
        emailService.notifyOwnerDepositReceived(deposit);

        return toConfirmDepositResponse(deposit);
    }

    @Transactional
    @Override
    public ConfirmDepositResponse confirmedNotReceived(String depositId) {
        Deposit deposit = getDepositOrThrow(depositId);

        if (deposit.getDepositStatus() != DepositStatus.CHO_XAC_NHAN) {
            throw new AppException(ErrorCode.CONTRACT_NOT_ALLOW_CONFIRM_DEPOSIT);
        }

        deposit.setDepositStatus(DepositStatus.CHUA_NHAN_COC);
        deposit.setUpdatedAt(Instant.now());

        deposit = depositRepository.save(deposit);
        emailService.notifyOwnerDepositNotReceived(deposit);

        return toConfirmDepositResponse(deposit);
    }

    @Override
    public void deleteDepositById(String depositId) {
        Deposit deposit = getDepositOrThrow(depositId);

        Contract contract = deposit.getContract();

        if (contract.getStatus() == ContractStatus.HIEU_LUC
                || contract.getStatus() == ContractStatus.SAP_HET_HAN
        ) {
            throw new AppException(ErrorCode.CONTRACT_NOT_ALLOW_DELETE_DEPOSIT);
        }

        depositRepository.deleteById(deposit.getId());
    }

    private Deposit getDepositOrThrow(String depositId) {
        return depositRepository.findById(depositId)
                .orElseThrow(() -> new AppException(ErrorCode.DEPOSIT_NOT_FOUND));
    }

    private ConfirmDepositResponse toConfirmDepositResponse(Deposit deposit) {
        return ConfirmDepositResponse.builder()
                .id(deposit.getId())
                .depositStatus(deposit.getDepositStatus())
                .build();
    }

}
