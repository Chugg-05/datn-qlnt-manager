package com.example.datn_qlnt_manager.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.datn_qlnt_manager.dto.statistics.revenue.DamageOverdueResponse;
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
        JOIN FETCH c.contractTenants ct
        JOIN FETCH ct.tenant t
        WHERE b.user.id = :ownerId
        AND  ct.representative = true
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
        JOIN FETCH c.contractTenants ct
        JOIN FETCH ct.tenant t
        WHERE b.user.id = :ownerId
        AND ct.representative = true
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
			i.ownerPhoneNumber,
			i.buildingName,
			i.buildingAddress,
			i.roomCode,
			i.tenantName,
			i.tenantPhoneNumber,
			i.month,
			i.year,
			i.paymentDueDate,
			i.invoiceStatus,
			i.invoiceType,
			i.totalAmount,
			i.note,
			i.createdAt,
			i.updatedAt
		)
		FROM Invoice i
		JOIN i.contract c
		JOIN c.room r
		JOIN r.floor f
		JOIN f.building b
		JOIN c.contractTenants ct
		JOIN ct.tenant t
		WHERE i.id = :invoiceId
		AND ct.representative = true
	""")
	Optional<InvoiceDetailView> getInvoiceDetailById(@Param("invoiceId") String invoiceId);

	@Query(
			"""
                SELECT COUNT(i),
                    SUM(CASE WHEN i.invoiceStatus = 'CHUA_THANH_TOAN' THEN 1 ELSE 0 END),
                    SUM(CASE WHEN i.invoiceStatus = 'CHO_THANH_TOAN' THEN 1 ELSE 0 END),
                    SUM(CASE WHEN i.invoiceStatus = 'DA_THANH_TOAN' THEN 1 ELSE 0 END),
                    SUM(CASE WHEN i.invoiceStatus = 'KHONG_THE_THANH_TOAN' THEN 1 ELSE 0 END),
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
        JOIN FETCH c.contractTenants ct
        JOIN FETCH ct.tenant t
        WHERE t.user.id = :userId
        AND ct.representative = true
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

	@Query("""
        SELECT i FROM Invoice i
        JOIN FETCH i.contract c
        JOIN FETCH c.contractTenants ct
        JOIN FETCH ct.tenant t
        WHERE i.invoiceStatus = :status
        AND i.month = :month
        AND i.year = :year
    """)
	List<Invoice> findAllByStatusAndMonth(
			@Param("status") InvoiceStatus status,
			@Param("month") int month,
			@Param("year") int year
	);

	@Query("""
		SELECT new com.example.datn_qlnt_manager.dto.statistics.revenue.DamageOverdueResponse(
			COALESCE(SUM(CASE WHEN i.invoiceStatus = 'KHONG_THE_THANH_TOAN' THEN i.totalAmount ELSE :zero END), :zero),
			COALESCE(SUM(CASE WHEN i.invoiceStatus = 'QUA_HAN' THEN i.totalAmount ELSE :zero END), :zero)
		)
		FROM Invoice i
		JOIN i.contract c
		JOIN c.room r
		JOIN r.floor f
		JOIN f.building b
		WHERE b.user.id = :userId
		AND  i.month = :month
	 	AND i.year = :year
		AND (:buildingId IS NULL OR b.id = :buildingId)
	""")
	DamageOverdueResponse getDamageAndOverdueAmount(
		    @Param("userId") String userId,
		    @Param("month") int month,
		    @Param("year") int year,
			@Param("buildingId") String buildingId,
		    @Param("zero") BigDecimal zero
	);

	List<Invoice> findByPaymentDueDateBefore(LocalDate date);

	boolean existsByContractIdAndMonthAndYearAndInvoiceType(
			String contractId, int month, int year, InvoiceType invoiceType);

	List<Invoice> findAllByInvoiceStatusAndDeleteAtBefore(InvoiceStatus invoiceStatus, LocalDate deleteAtBefore);

}
