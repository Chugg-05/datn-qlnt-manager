package com.example.datn_qlnt_manager.repository;

import java.math.BigDecimal;
import java.util.List;

import com.example.datn_qlnt_manager.common.InvoiceStatus;
import com.example.datn_qlnt_manager.dto.statistics.revenue.InvoiceRevenueResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.entity.InvoiceDetail;

@Repository
public interface InvoiceDetailsRepository extends JpaRepository<InvoiceDetail, String> {

    @Query("""
        SELECT new com.example.datn_qlnt_manager.dto.statistics.revenue.InvoiceRevenueResponse(
            COALESCE(SUM(CASE
                WHEN i.invoiceStatus IN (:statuses)
                     AND i.month = :month
                     AND i.year = :year
                     AND (d.invoiceItemType IS NULL OR d.invoiceItemType <> 'DEN_BU')
                THEN d.amount ELSE :zero END), :zero),
    
            COALESCE(SUM(CASE
                WHEN i.invoiceStatus = 'DA_THANH_TOAN'
                     AND i.year = :year
                     AND i.month = :month
                     AND (d.invoiceItemType IS NULL OR d.invoiceItemType <> 'DEN_BU')
                THEN d.amount ELSE :zero END), :zero),
    
            COALESCE(SUM(CASE
                WHEN i.invoiceStatus = 'DA_THANH_TOAN'
                     AND d.invoiceItemType = 'TIEN_PHONG'
                THEN d.amount ELSE :zero END), :zero),
    
            COALESCE(SUM(CASE
                WHEN i.invoiceStatus = 'DA_THANH_TOAN'
                     AND d.invoiceItemType = 'DIEN'
                THEN d.amount ELSE :zero END), :zero),
    
            COALESCE(SUM(CASE
                WHEN i.invoiceStatus = 'DA_THANH_TOAN'
                     AND d.invoiceItemType = 'NUOC'
                THEN d.amount ELSE :zero END), :zero),
    
            COALESCE(SUM(CASE
                WHEN i.invoiceStatus = 'DA_THANH_TOAN'
                     AND d.invoiceItemType = 'DICH_VU'
                THEN d.amount ELSE :zero END), :zero),
    
            COALESCE(SUM(CASE
                WHEN i.invoiceStatus = 'DA_THANH_TOAN'
                     AND d.invoiceItemType = 'DEN_BU'
                THEN d.amount ELSE :zero END), :zero)
        )
        FROM InvoiceDetail d
        JOIN d.invoice i
        JOIN i.contract c
        JOIN c.room r
        JOIN r.floor f
        JOIN f.building b
        WHERE b.user.id = :userId
        AND  i.month = :month
        AND i.year = :year
        AND (:buildingId IS NULL OR b.id = :buildingId)
    """)
    InvoiceRevenueResponse getInvoiceRevenueStatistics(
            @Param("statuses") List<InvoiceStatus> statuses,
            @Param("userId") String userId,
            @Param("month") int month,
            @Param("year") int year,
            @Param("buildingId") String buildingId,
            @Param("zero") BigDecimal zero
    );

    @Query(
    """
        SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END
        FROM InvoiceDetail d
        JOIN d.invoice i
        JOIN i.contract c
        JOIN c.room r
        WHERE d.oldIndex = :oldIndex
        AND d.newIndex = :newIndex
        AND i.month = :month
        AND i.year = :year
        AND r.id = (
            SELECT m.room.id FROM Meter m WHERE m.id = :meterId
        )
    """)
    boolean existsByOldIndexAndNewIndexAndMonthAndYearAndMeterId(
            @Param("oldIndex") int oldIndex,
            @Param("newIndex") int newIndex,
            @Param("month") int month,
            @Param("year") int year,
            @Param("meterId") String meterId);

    List<InvoiceDetail> findByInvoiceId(String invoiceId);
}
