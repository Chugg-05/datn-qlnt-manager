package com.example.datn_qlnt_manager.repository;

import java.math.BigDecimal;
import java.util.List;

import com.example.datn_qlnt_manager.common.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.entity.InvoiceDetail;

@Repository
public interface InvoiceDetailsRepository extends JpaRepository<InvoiceDetail, String> {

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

    //Doanh thu dự kiến
    @Query("""
        SELECT COALESCE(SUM(d.amount), 0)
        FROM InvoiceDetail d
        JOIN d.invoice i
        WHERE i.contract.room.floor.building.user.id = :userId
          AND i.invoiceStatus IN (:statuses)
          AND i.month = :month
          AND i.year = :year
          AND i.contract.room.floor.building.id = :buildingId
          AND (d.invoiceItemType IS NULL OR d.invoiceItemType != 'DEN_BU')
    """)
    BigDecimal getExpectedRevenue(
            @Param("userId") String userId,
            @Param("statuses") List<InvoiceStatus> statuses,
            @Param("month") Integer month,
            @Param("year") int year,
            @Param("buildingId") String buildingId);

    //Doanh thu thực tế
    @Query("""
        SELECT COALESCE(SUM(d.amount), 0)
        FROM InvoiceDetail d
        JOIN d.invoice i
        WHERE i.contract.room.floor.building.user.id = :userId
        AND i.invoiceStatus = 'DA_THANH_TOAN'
          AND i.month = :month
          AND i.year = :year
          AND i.contract.room.floor.building.id = :buildingId
          AND (d.invoiceItemType IS NULL OR d.invoiceItemType != 'DEN_BU')
    """)
    BigDecimal getCurrentRevenue(
            @Param("userId") String userId,
            @Param("month") Integer month,
            @Param("year") int year,
            @Param("buildingId") String buildingId);

    //Tiền phòng
    @Query("""
        SELECT COALESCE(SUM(d.amount), 0)
        FROM InvoiceDetail d
        JOIN d.invoice i
        WHERE i.contract.room.floor.building.user.id = :userId
          AND i.invoiceStatus = 'DA_THANH_TOAN'
          AND d.invoiceItemType = 'TIEN_PHONG'
          AND (:month IS NULL OR i.month = :month)
          AND i.year = :year
          AND i.contract.room.floor.building.id = :buildingId
    """)
    BigDecimal getRoomRevenue(
            @Param("userId") String userId,
            @Param("month") Integer month,
            @Param("year") int year,
            @Param("buildingId") String buildingId);

    //Tiền điện nước
    @Query("""
        SELECT COALESCE(SUM(d.amount), 0)
        FROM InvoiceDetail d
        JOIN d.invoice i
        WHERE i.contract.room.floor.building.user.id = :userId
          AND i.invoiceStatus = 'DA_THANH_TOAN'
          AND d.invoiceItemType = 'DIEN'
          AND i.month = :month
          AND i.year = :year
          AND i.contract.room.floor.building.id = :buildingId
    """)
    BigDecimal getElectricRevenue(
            @Param("userId") String userId,
            @Param("month") Integer month,
            @Param("year") int year,
            @Param("buildingId") String buildingId);

    @Query("""
        SELECT COALESCE(SUM(d.amount), 0)
        FROM InvoiceDetail d
        JOIN d.invoice i
        WHERE i.contract.room.floor.building.user.id = :userId
          AND i.invoiceStatus = 'DA_THANH_TOAN'
          AND d.invoiceItemType = 'NUOC'
          AND i.month = :month
          AND i.year = :year
          AND i.contract.room.floor.building.id = :buildingId
    """)
    BigDecimal getWaterRevenue(
            @Param("userId") String userId,
            @Param("month") Integer month,
            @Param("year") int year,
            @Param("buildingId") String buildingId);

    //Tiền dịch vụ
    @Query("""
    SELECT COALESCE(SUM(d.amount), 0)
    FROM InvoiceDetail d
    JOIN d.invoice i
    WHERE i.contract.room.floor.building.user.id = :userId
      AND i.invoiceStatus = 'DA_THANH_TOAN'
      AND d.invoiceItemType = 'DICH_VU'
      AND i.month = :month
      AND i.year = :year
      AND i.contract.room.floor.building.id = :buildingId
    """)
    BigDecimal getServiceRevenue(
            @Param("userId") String userId,
            @Param("month") Integer month,
            @Param("year") int year,
            @Param("buildingId") String buildingId);

    @Query("""
        SELECT COALESCE(SUM(d.amount), 0)
        FROM InvoiceDetail d
        JOIN d.invoice i
        WHERE i.contract.room.floor.building.user.id = :userId
          AND i.invoiceStatus = 'QUA_HAN'
          AND i.month = :month
          AND i.year = :year
          AND i.contract.room.floor.building.id = :buildingId
          AND (d.invoiceItemType IS NULL OR d.invoiceItemType != 'DEN_BU')
    """)
    BigDecimal getOverdueRevenue(
            @Param("userId") String userId,
            @Param("month") Integer month,
            @Param("year") int year,
            @Param("buildingId") String buildingId);

    @Query("""
        SELECT COALESCE(SUM(d.amount), 0)
        FROM InvoiceDetail d
        JOIN d.invoice i
        WHERE i.contract.room.floor.building.user.id = :userId
          AND i.invoiceStatus = 'KHONG_THE_THANH_TOAN'
          AND i.month = :month
          AND i.year = :year
          AND i.contract.room.floor.building.id = :buildingId
          AND (d.invoiceItemType IS NULL OR d.invoiceItemType <> 'DEN_BU')
    """)
    BigDecimal getUncollectibleRevenue(
            @Param("userId") String userId,
            @Param("month") Integer month,
            @Param("year") int year,
            @Param("buildingId") String buildingId);

    List<InvoiceDetail> findByInvoiceId(String invoiceId);
}
