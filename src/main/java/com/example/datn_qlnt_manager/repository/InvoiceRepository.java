package com.example.datn_qlnt_manager.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.common.InvoiceStatus;
import com.example.datn_qlnt_manager.common.InvoiceType;
import com.example.datn_qlnt_manager.dto.projection.InvoiceDetailView;
import com.example.datn_qlnt_manager.dto.statistics.InvoiceStatistics;
import com.example.datn_qlnt_manager.entity.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    @Query(
            """
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
		AND (:minTotalAmount IS NULL OR i.totalAmount >= :minTotalAmount)
		AND (:maxTotalAmount IS NULL OR i.totalAmount <= :maxTotalAmount)
		AND (:invoiceStatus IS NULL OR i.invoiceStatus = :invoiceStatus)
		AND (:invoiceType IS NULL OR i.invoiceType = :invoiceType)
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
            @Param("minTotalAmount") BigDecimal minTotalAmount,
            @Param("maxTotalAmount") BigDecimal maxTotalAmount,
            @Param("invoiceStatus") InvoiceStatus invoiceStatus,
            @Param("invoiceType") InvoiceType invoiceType,
            Pageable pageable);

    @Query(
            """
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
		AND (:minTotalAmount IS NULL OR i.totalAmount >= :minTotalAmount)
		AND (:maxTotalAmount IS NULL OR i.totalAmount <= :maxTotalAmount)
		AND (:invoiceType IS NULL OR i.invoiceType = :invoiceType)
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
            @Param("minTotalAmount") BigDecimal minTotalAmount,
            @Param("maxTotalAmount") BigDecimal maxTotalAmount,
            @Param("invoiceType") InvoiceType invoiceType,
            Pageable pageable);

    @Query(
            """
		SELECT new com.example.datn_qlnt_manager.dto.projection.InvoiceDetailView(
			i.id,
			i.invoiceCode,
			i.month,
			i.year,
			i.paymentDueDate,
			i.invoiceStatus,
			i.invoiceType,
			i.totalAmount,
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

    @Query(
            """
				SELECT COUNT(i),
					SUM(CASE WHEN i.invoiceStatus = 'CHUA_THANH_TOAN' THEN 1 ELSE 0 END),
					SUM(CASE WHEN i.invoiceStatus = 'DA_THANH_TOAN' THEN 1 ELSE 0 END),
					SUM(CASE WHEN i.invoiceStatus = 'QUA_HAN' THEN 1 ELSE 0 END),
					SUM(CASE WHEN i.invoiceStatus = 'HUY' THEN 1 ELSE 0 END)
				FROM Invoice i
				WHERE i.contract.room.floor.building.user.id = :userId
			""")
    InvoiceStatistics getTotalInvoiceByStatus(@Param("userId") String userId);

    @Query(
            """
				SELECT i FROM Invoice i
				WHERE i.contract.room.floor.building.user.id = :userId
				ORDER BY i.updatedAt DESC
			""")
    List<Invoice> findAllInvoicesByUserId(@Param("userId") String userId);

    @Query(
            """
		SELECT i
		FROM Invoice i
		JOIN FETCH i.contract c
		JOIN FETCH c.room r
		JOIN FETCH r.floor f
		JOIN FETCH f.building b
		JOIN FETCH c.tenants t
		WHERE t.user.id = :userId
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
		AND (:minTotalAmount IS NULL OR i.totalAmount >= :minTotalAmount)
		AND (:maxTotalAmount IS NULL OR i.totalAmount <= :maxTotalAmount)
		AND (:invoiceStatus IS NULL OR i.invoiceStatus = :invoiceStatus)
		AND (:invoiceType IS NULL OR i.invoiceType = :invoiceType)
		AND i.invoiceStatus IN :statusList
		ORDER BY i.updatedAt DESC
	""")
    Page<Invoice> getInvoicesForTenant(
            @Param("userId") String userId,
            @Param("query") String query,
            @Param("building") String building,
            @Param("floor") String floor,
            @Param("month") Integer month,
            @Param("year") Integer year,
            @Param("minTotalAmount") BigDecimal minTotalAmount,
            @Param("maxTotalAmount") BigDecimal maxTotalAmount,
            @Param("invoiceStatus") InvoiceStatus invoiceStatus,
            @Param("invoiceType") InvoiceType invoiceType,
            @Param("statusList") List<InvoiceStatus> statusList,
            Pageable pageable);

    @Query(
            """
		SELECT i FROM Invoice i
		JOIN FETCH i.contract c
		JOIN FETCH c.tenants t
		WHERE i.invoiceStatus = :status
		AND i.month = :month
		AND i.year = :year
	""")
    List<Invoice> findAllByStatusAndMonth(
            @Param("status") InvoiceStatus status, @Param("month") int month, @Param("year") int year);

    boolean existsByContractIdAndMonthAndYearAndInvoiceType(
            String contractId, int month, int year, InvoiceType invoiceType);

    boolean existsByContractIdAndMonthAndYear(String contractId, Integer month, Integer year);
}
