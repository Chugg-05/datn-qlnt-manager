package com.example.datn_qlnt_manager.repository;

import org.springframework.stereotype.Repository;
import com.example.datn_qlnt_manager.entity.PaymentHistory;
import com.example.datn_qlnt_manager.common.PaymentMethod;
import com.example.datn_qlnt_manager.common.PaymentStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, String> {

    @Query("""
        SELECT ph FROM PaymentHistory ph
        JOIN ph.paymentReceipt pr
        JOIN pr.invoice i
        JOIN i.contract c
        WHERE (:query IS NULL OR LOWER(pr.receiptCode) LIKE LOWER(CONCAT('%', :query, '%'))
                           OR LOWER(i.invoiceCode) LIKE LOWER(CONCAT('%', :query, '%')))
          AND (:paymentStatus IS NULL OR pr.paymentStatus = :paymentStatus)
          AND (:paymentMethod IS NULL OR pr.paymentMethod = :paymentMethod)
          AND (:fromAmount IS NULL OR pr.amount >= :fromAmount)
          AND (:toAmount IS NULL OR pr.amount <= :toAmount)
          AND (:fromDate IS NULL OR pr.paymentDate >= :fromDate)
          AND (:toDate IS NULL OR pr.paymentDate <= :toDate)
          AND (c.room.floor.building.user.id = :userId)
    """)
    Page<PaymentHistory> filterPaymentHistories(
            @Param("userId") String userId,
            @Param("query") String query,
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("paymentMethod") PaymentMethod paymentMethod,
            @Param("fromAmount") BigDecimal fromAmount,
            @Param("toAmount") BigDecimal toAmount,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);

    @Query("""
    SELECT ph FROM PaymentHistory ph
    JOIN ph.paymentReceipt pr
    JOIN pr.invoice i
    JOIN i.contract c
    JOIN c.contractTenants ct
    JOIN ct.tenant t
    JOIN t.user u
    WHERE u.id = :userId
      AND (:query IS NULL OR LOWER(pr.receiptCode) LIKE LOWER(CONCAT('%', :query, '%'))
                         OR LOWER(i.invoiceCode) LIKE LOWER(CONCAT('%', :query, '%')))
      AND (:paymentStatus IS NULL OR pr.paymentStatus = :paymentStatus)
      AND (:paymentMethod IS NULL OR pr.paymentMethod = :paymentMethod)
      AND (:fromAmount IS NULL OR pr.amount >= :fromAmount)
      AND (:toAmount IS NULL OR pr.amount <= :toAmount)
      AND (:fromDate IS NULL OR pr.paymentDate >= :fromDate)
      AND (:toDate IS NULL OR pr.paymentDate <= :toDate)
""")
    Page<PaymentHistory> filterPaymentHistoriesByTenant(
            @Param("userId") String userId,
            @Param("query") String query,
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("paymentMethod") PaymentMethod paymentMethod,
            @Param("fromAmount") BigDecimal fromAmount,
            @Param("toAmount") BigDecimal toAmount,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
}
