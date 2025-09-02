package com.example.datn_qlnt_manager.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.common.ContractStatus;
import com.example.datn_qlnt_manager.common.Gender;
import com.example.datn_qlnt_manager.dto.response.contract.ContractDetailResponse;
import com.example.datn_qlnt_manager.dto.statistics.ContractStatistics;
import com.example.datn_qlnt_manager.entity.Contract;

@Repository
public interface ContractRepository extends JpaRepository<Contract, String> {

    @Query(
            """
		SELECT c FROM Contract c
		INNER JOIN c.contractTenants ct
		WHERE (c.room.floor.building.user.id = :userId)
		AND ((:query IS NULL OR c.contractCode LIKE CONCAT('%', :query, '%') )
		OR (:query IS NULL OR  c.room.roomCode LIKE  CONCAT('%', :query, '%') )
		OR (:query IS NULL OR  ct.tenant.fullName LIKE  CONCAT('%', :query, '%') )
		OR (:query IS NULL OR  ct.tenant.phoneNumber LIKE  CONCAT('%', :query, '%') )
		OR (:query IS NULL OR  ct.tenant.identityCardNumber LIKE  CONCAT('%', :query, '%') )
		OR (:query IS NULL OR ct.tenant.email LIKE CONCAT('%', :query, '%') ))
		AND (:building IS NULL OR c.room.floor.building.id = :building)
		AND (:gender IS NULL OR ct.tenant.gender = :gender)
		AND (:status IS NULL OR c.status = :status)
		AND c.status != 'DA_HUY'
		ORDER BY c.updatedAt DESC
	""")
    Page<Contract> getPageAndSearchAndFilterContractByUserId(
            @Param("userId") String userId,
            @Param("query") String query,
            @Param("building") String building,
            @Param("gender") Gender gender,
            @Param("status") ContractStatus status,
            Pageable pageable);

    @Query(
            """
		SELECT c FROM Contract c
		INNER JOIN c.contractTenants ct
		WHERE (c.room.floor.building.user.id = :userId)
		AND ((:query IS NULL OR c.contractCode LIKE CONCAT('%', :query, '%') )
		OR (:query IS NULL OR  c.room.roomCode LIKE  CONCAT('%', :query, '%') )
		OR (:query IS NULL OR  ct.tenant.fullName LIKE  CONCAT('%', :query, '%') )
		OR (:query IS NULL OR  ct.tenant.phoneNumber LIKE  CONCAT('%', :query, '%') )
		OR (:query IS NULL OR  ct.tenant.identityCardNumber LIKE  CONCAT('%', :query, '%') )
		OR (:query IS NULL OR ct.tenant.email LIKE CONCAT('%', :query, '%') ))
		AND (:building IS NULL OR c.room.floor.building.id = :building)
		AND (:gender IS NULL OR ct.tenant.gender = :gender)
		AND c.status = 'DA_HUY'
		ORDER BY c.updatedAt DESC
	""")
    Page<Contract> getContractWithStatusCancelByUserId(
            @Param("userId") String userId,
            @Param("query") String query,
			@Param("building") String building,
            @Param("gender") Gender gender,
            Pageable pageable);

    @Query(
	"""
		SELECT new com.example.datn_qlnt_manager.dto.response.contract.ContractDetailResponse(
			c.id,
			r.id,
			c.contractCode,
			r.roomCode,
			owner.fullName,
			owner.phoneNumber,
			t.fullName,
			t.email,
			t.phoneNumber,
			t.identityCardNumber,
			t.address,
			c.startDate,
			c.endDate,
			c.deposit,
			c.roomPrice,
			b.address,
			c.status,
			c.electricPrice,
			c.waterPrice,
			c.createdAt,
			c.updatedAt,
			c.content
		)
		FROM Contract c
		JOIN c.room r
		JOIN r.floor f
		JOIN f.building b
		JOIN b.user owner
		JOIN c.contractTenants ct
		JOIN ct.tenant t
		WHERE c.id = :contractId
		AND ct.representative = true
	""")
    Optional<ContractDetailResponse> findContractDetailById(@Param("contractId") String contractId);

    @Query(
            """
		SELECT c FROM Contract c
		JOIN c.room r
		JOIN r.floor f
		JOIN f.building b
		JOIN b.user u
		WHERE u.id = :userId AND c.status = 'HIEU_LUC'
		ORDER BY c.updatedAt DESC
	""")
    List<Contract> findAllContractByUserId(@Param("userId") String userId);

    @Query(
            """
		SELECT COUNT(c),
			SUM(CASE WHEN c.status = 'CHO_KICH_HOAT' THEN 1 ELSE 0 END),
			SUM(CASE WHEN c.status = 'HIEU_LUC' THEN 1 ELSE 0 END),
			SUM(CASE WHEN c.status = 'SAP_HET_HAN' THEN 1 ELSE 0 END),
			SUM(CASE WHEN c.status = 'KET_THUC_DUNG_HAN' THEN 1 ELSE 0 END),
			SUM(CASE WHEN c.status = 'KET_THUC_CO_BAO_TRUOC' THEN 1 ELSE 0 END),
			SUM(CASE WHEN c.status = 'TU_Y_HUY_BO' THEN 1 ELSE 0 END)
		FROM Contract c
		WHERE c.room.floor.building.user.id = :userId
	""")
    ContractStatistics getTotalContractByStatus(@Param("userId") String userId);

    @Query("""
		SELECT c FROM Contract c
		WHERE c.startDate <= :endOfMonth
		AND c.endDate >= :startOfMonth
	""")
    List<Contract> findValidContractsInMonth(
            @Param("startOfMonth") LocalDate startOfMonth, @Param("endOfMonth") LocalDate endOfMonth);

	@Query("""
		SELECT c FROM Contract c
		WHERE c.room.id = :roomId
		AND c.status = 'HIEU_LUC'
	""")
	Optional<Contract> findActiveContractByRoomId(@Param("roomId") String roomId);

	@Query("""
    SELECT DISTINCT c
    FROM Contract c
    JOIN c.contractTenants ct
    JOIN ct.tenant.user u
    WHERE u.id = :userId
      AND (:query IS NULL OR LOWER(c.contractCode) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(c.room.roomCode) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(ct.tenant.fullName) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(ct.tenant.phoneNumber) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(ct.tenant.identityCardNumber) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(ct.tenant.email) LIKE LOWER(CONCAT('%', :query, '%')))
      AND (:gender IS NULL OR ct.tenant.gender = :gender)
      AND (:status IS NULL OR c.status = :status)
      AND c.status != 'DA_HUY'
    ORDER BY c.updatedAt DESC
""")
	Page<Contract> getPageAndSearchAndFilterContractByTenantUserId(
			@Param("userId") String userId,
			@Param("query") String query,
			@Param("gender") Gender gender,
			@Param("status") ContractStatus status,
			Pageable pageable
	);

	@Query("""
        SELECT c FROM Contract c
        JOIN c.contractTenants ct
        WHERE ct.tenant.id = :tenantId
    """)
	List<Contract> findAllByTenantId(@Param("tenantId") String tenantId);

	@Query("""
		SELECT c
		FROM Contract c
		JOIN c.room r
		JOIN r.floor f
		JOIN f.building b
		WHERE b.id = :buildingId
		  AND c.startDate <= :endOfMonth
		  AND (c.endDate IS NULL OR c.endDate >= :startOfMonth)
	""")
	List<Contract> findActiveContractsByBuildingAndMonthYear(
			@Param("buildingId") String buildingId,
			@Param("startOfMonth") LocalDate startOfMonth,
			@Param("endOfMonth") LocalDate endOfMonth
	);

	List<Contract> findByEndDateBefore(LocalDate endDate); // Thêm phương thức mới
	List<Contract> findByEndDateBetween(LocalDate startDate, LocalDate endDate);

	List<Contract> findAllByStatusAndDeletedAtBefore(ContractStatus status, LocalDate deletedAtBefore);

	boolean existsByRoomIdAndEndDateAfter(String roomId, LocalDate startDate);

	boolean existsByRoomIdAndStatusIn(String roomId, List<ContractStatus> statuses);

	boolean existsByRoom_Floor_Building_IdAndStatusIn(String buildingId, List<ContractStatus> statuses);

	boolean existsByRoom_Floor_IdAndStatusIn(String floorId, List<ContractStatus> statuses);

	@Query("""
    SELECT c
    FROM Contract c
    JOIN c.contractTenants ct
    JOIN ct.tenant t
    WHERE t.id = :tenantId
      AND c.status IN :statuses
      AND c.deletedAt IS NULL
""")
	List<Contract> findByTenantIdAndStatusIn(
			@Param("tenantId") String tenantId,
			@Param("statuses") List<ContractStatus> statuses
	);


}
