package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.common.PaymentMethod;
import com.example.datn_qlnt_manager.common.PaymentStatus;
import com.example.datn_qlnt_manager.entity.PaymentReceipt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PaymentReceiptRepository extends JpaRepository<PaymentReceipt, String> {

    @Query("""
    SELECT r FROM PaymentReceipt r
    JOIN r.invoice i
    JOIN i.contract c
    WHERE (:query IS NULL OR LOWER(r.receiptCode) LIKE LOWER(CONCAT('%', :query, '%'))
                         OR LOWER(i.invoiceCode) LIKE LOWER(CONCAT('%', :query, '%')))
      AND (:paymentStatus IS NULL OR r.paymentStatus = :paymentStatus)
      AND (:paymentMethod IS NULL OR r.paymentMethod = :paymentMethod)
      AND (:fromAmount IS NULL OR r.amount >= :fromAmount)
      AND (:toAmount IS NULL OR r.amount <= :toAmount)
      AND (:fromDate IS NULL OR r.paymentDate >= :fromDate)
      AND (:toDate IS NULL OR r.paymentDate <= :toDate)
""")
    Page<PaymentReceipt> filterPaymentReceipts(
            @Param("query") String query,
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("paymentMethod") PaymentMethod paymentMethod,
            @Param("fromAmount") BigDecimal fromAmount,
            @Param("toAmount") BigDecimal toAmount,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    boolean existsByInvoiceId(String invoiceId);

}
