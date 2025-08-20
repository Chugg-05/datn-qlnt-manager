package com.example.datn_qlnt_manager.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.datn_qlnt_manager.common.PaymentMethod;
import com.example.datn_qlnt_manager.common.PaymentStatus;
import com.example.datn_qlnt_manager.entity.PaymentReceipt;

public interface PaymentReceiptRepository extends JpaRepository<PaymentReceipt, String> {

	@Query(
			"""
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
    AND (c.room.floor.building.user.id = :userId)
    AND (r.paymentStatus != 'HUY')
""")
	Page<PaymentReceipt> filterPaymentReceipts(
			@Param("userId") String userId,
			@Param("query") String query,
			@Param("paymentStatus") PaymentStatus paymentStatus,
			@Param("paymentMethod") PaymentMethod paymentMethod,
			@Param("fromAmount") BigDecimal fromAmount,
			@Param("toAmount") BigDecimal toAmount,
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate,
			Pageable pageable);

	boolean existsByInvoiceId(String invoiceId);

	@Query(
			"""
    SELECT r FROM PaymentReceipt r
    JOIN r.invoice i
    JOIN i.contract c
    JOIN c.tenants t
    WHERE t.id = :tenantId
    AND (:query IS NULL OR LOWER(r.receiptCode) LIKE LOWER(CONCAT('%', :query, '%'))
                        OR LOWER(i.invoiceCode) LIKE LOWER(CONCAT('%', :query, '%')))
    AND (:paymentStatus IS NULL OR r.paymentStatus = :paymentStatus)
    AND (:paymentMethod IS NULL OR r.paymentMethod = :paymentMethod)
    AND (:fromAmount IS NULL OR r.amount >= :fromAmount)
    AND (:toAmount IS NULL OR r.amount <= :toAmount)
    AND (:fromDate IS NULL OR r.paymentDate >= :fromDate)
    AND (:toDate IS NULL OR r.paymentDate <= :toDate)
    AND (r.paymentStatus != 'HUY')
    ORDER BY r.updatedAt DESC
""")
	Page<PaymentReceipt> findAllByTenantId(
			@Param("tenantId") String tenantId,
			@Param("query") String query,
			@Param("paymentStatus") PaymentStatus paymentStatus,
			@Param("paymentMethod") PaymentMethod paymentMethod,
			@Param("fromAmount") BigDecimal fromAmount,
			@Param("toAmount") BigDecimal toAmount,
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate,
			Pageable pageable);

	@Query("""
			SELECT r FROM PaymentReceipt r JOIN r.invoice i WHERE i.id = :invoiceId
			""")
	PaymentReceipt findByInvoiceId(String invoiceId);
}