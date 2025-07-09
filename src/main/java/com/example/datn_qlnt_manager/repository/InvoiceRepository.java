package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.common.InvoiceStatus;
import com.example.datn_qlnt_manager.dto.projection.InvoiceDetailView;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceItemResponse;
import com.example.datn_qlnt_manager.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    @Query("""
        SELECT i
        FROM Invoice i
        JOIN FETCH i.contract c
        JOIN FETCH c.room r
        JOIN FETCH r.floor f
        JOIN FETCH f.building b
        JOIN FETCH c.tenants t
        WHERE b.user.id = :ownerId
        AND t.isRepresentative = true
        AND (
          :query IS NULL
          OR i.invoiceCode LIKE CONCAT('%', :query, '%')
          OR b.buildingName LIKE CONCAT('%', :query, '%')
          OR r.roomCode LIKE CONCAT('%', :query, '%')
          OR t.fullName LIKE CONCAT('%', :query, '%')
          OR t.phoneNumber LIKE CONCAT('%', :query, '%')
          OR t.identityCardNumber LIKE CONCAT('%', :query, '%')
        )
        AND (:building IS NULL OR b.id = :building)
        AND (:floor IS NULL OR f.id = :floor)
        AND (:month IS NULL OR i.month = :month)
        AND (:year IS NULL OR i.year = :year)
        AND (:minGrandTotal IS NULL OR i.grandTotal >= :minGrandTotal)
        AND (:maxGrandTotal IS NULL OR i.grandTotal <= :maxGrandTotal)
        AND (:invoiceStatus IS NULL OR i.invoiceStatus = :invoiceStatus)
        AND i.invoiceStatus != 'HUY'
        ORDER BY i.updatedAt DESC
    """)
    Page<Invoice> getPageAnsSearchAndFilterInvoiceByOwnerId(
            @Param("ownerId") String ownerId,
            @Param("query") String query,
            @Param("building") String building,
            @Param("floor") String floor,
            @Param("month") Integer month,
            @Param("year") Integer year,
            @Param("minGrandTotal") BigDecimal minGrandTotal,
            @Param("maxGrandTotal") BigDecimal maxGrandTotal,
            @Param("invoiceStatus") InvoiceStatus invoiceStatus,
            Pageable pageable
    );

    @Query("""
        SELECT i
        FROM Invoice i
        JOIN FETCH i.contract c
        JOIN FETCH c.room r
        JOIN FETCH r.floor f
        JOIN FETCH f.building b
        JOIN FETCH c.tenants t
        WHERE b.user.id = :ownerId
        AND t.isRepresentative = true
        AND (
          :query IS NULL
          OR i.invoiceCode LIKE CONCAT('%', :query, '%')
          OR b.buildingName LIKE CONCAT('%', :query, '%')
          OR r.roomCode LIKE CONCAT('%', :query, '%')
          OR t.fullName LIKE CONCAT('%', :query, '%')
          OR t.phoneNumber LIKE CONCAT('%', :query, '%')
          OR t.identityCardNumber LIKE CONCAT('%', :query, '%')
        )
        AND (:building IS NULL OR b.id = :building)
        AND (:floor IS NULL OR f.id = :floor)
        AND (:month IS NULL OR i.month = :month)
        AND (:year IS NULL OR i.year = :year)
        AND (:minGrandTotal IS NULL OR i.grandTotal >= :minGrandTotal)
        AND (:maxGrandTotal IS NULL OR i.grandTotal <= :maxGrandTotal)
        AND i.invoiceStatus = 'HUY'
        ORDER BY i.updatedAt DESC
    """)
    Page<Invoice> getInvoiceWithStatusCancelByUserId(
            @Param("ownerId") String ownerId,
            @Param("query") String query,
            @Param("building") String building,
            @Param("floor") String floor,
            @Param("month") Integer month,
            @Param("year") Integer year,
            @Param("minGrandTotal") BigDecimal minGrandTotal,
            @Param("maxGrandTotal") BigDecimal maxGrandTotal,
            Pageable pageable
    );

    @Query("""
        SELECT new com.example.datn_qlnt_manager.dto.projection.InvoiceDetailView(
            i.id,
            i.invoiceCode,
            i.month,
            i.year,
            i.paymentDueDate,
            i.invoiceStatus,
            i.grandTotal,
            i.note,
            i.createdAt,
            i.updatedAt,
            b.buildingName,
            r.roomCode,
            t.fullName,
            t.phoneNumber
        )
        FROM Invoice i
        JOIN i.contract c
        JOIN c.room r
        JOIN r.floor f
        JOIN f.building b
        JOIN c.tenants t
        WHERE i.id = :invoiceId
        AND t.isRepresentative = true
    """)
    Optional<InvoiceDetailView> getInvoiceDetailById(@Param("invoiceId") String invoiceId);

}
