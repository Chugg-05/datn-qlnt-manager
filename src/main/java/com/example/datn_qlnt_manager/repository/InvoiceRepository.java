package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.common.InvoiceStatus;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceResponse;
import com.example.datn_qlnt_manager.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    @Query("""
        SELECT new com.example.datn_qlnt_manager.dto.response.invoice.InvoiceResponse(
            i.id,
            i.invoiceCode,
            b.buildingName,
            r.roomCode,
            t.fullName,
            i.month,
            i.year,
            i.grandTotal,
            i.paymentDueDate,
            i.invoiceStatus,
            i.note,
            i.createdAt
        )
        FROM Invoice i
        JOIN i.contract c
        JOIN c.room r
        JOIN r.floor f
        JOIN f.building b
        JOIN c.tenants t
        WHERE b.user.id = :ownerId
        AND t.isRepresentative = true
        AND (:query IS NULL OR i.invoiceCode LIKE CONCAT('%', :query, '%'))
        AND (:query IS NULL OR b.buildingName LIKE CONCAT('%', :query, '%'))
        AND (:query IS NULL OR r.roomCode LIKE CONCAT('%', :query, '%'))
        AND (:query IS NULL OR t.fullName LIKE CONCAT('%', :query, '%'))
        AND (:query IS NULL OR t.phoneNumber LIKE CONCAT('%', :query, '%'))
        AND (:query IS NULL OR t.identityCardNumber LIKE CONCAT('%', :query, '%'))
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
    Page<InvoiceResponse> getPageAnsSearchAndFilterInvoiceByOwnerId(
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

}
