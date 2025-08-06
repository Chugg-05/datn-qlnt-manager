package com.example.datn_qlnt_manager.repository;

import java.time.LocalDateTime;
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
		INNER JOIN c.tenants t
		WHERE (c.room.floor.building.user.id = :userId)
		AND ((:query IS NULL OR c.contractCode LIKE CONCAT('%', :query, '%') )
		OR (:query IS NULL OR  c.room.roomCode LIKE  CONCAT('%', :query, '%') )
		OR (:query IS NULL OR  t.fullName LIKE  CONCAT('%', :query, '%') )
		OR (:query IS NULL OR  t.phoneNumber LIKE  CONCAT('%', :query, '%') )
		OR (:query IS NULL OR  t.identityCardNumber LIKE  CONCAT('%', :query, '%') )
		OR (:query IS NULL OR t.email LIKE CONCAT('%', :query, '%') ))
		AND (:building IS NULL OR c.room.floor.building.id = :building)
		AND (:gender IS NULL OR t.gender = :gender)
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
		INNER JOIN c.tenants t
		WHERE (c.room.floor.building.user.id = :userId)
		AND ((:query IS NULL OR c.contractCode LIKE CONCAT('%', :query, '%') )
		OR (:query IS NULL OR  c.room.roomCode LIKE  CONCAT('%', :query, '%') )
		OR (:query IS NULL OR  t.fullName LIKE  CONCAT('%', :query, '%') )
		OR (:query IS NULL OR  t.phoneNumber LIKE  CONCAT('%', :query, '%') )
		OR (:query IS NULL OR  t.identityCardNumber LIKE  CONCAT('%', :query, '%') )
		OR (:query IS NULL OR t.email LIKE CONCAT('%', :query, '%') ))
		AND (:building IS NULL OR c.room.floor.building.id = :building)
		AND (:gender IS NULL OR t.gender = :gender)
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
			c.contractCode,
			r.roomCode,
			owner.fullName,
			owner.phoneNumber,
			rep.fullName,
			rep.email,
			rep.phoneNumber,
			rep.identityCardNumber,
			rep.address,
			c.numberOfPeople,
			c.startDate,
			c.endDate,
			c.deposit,
			c.roomPrice,
			b.address,
			c.status,
			c.electricPrice,
			c.waterPrice,
			c.createdAt,
			c.updatedAt
		)
		FROM Contract c
		JOIN c.room r
		JOIN r.floor f
		JOIN f.building b
		JOIN b.user owner
		JOIN c.tenants rep
		WHERE c.id = :contractId AND rep.isRepresentative = true
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
			SUM(CASE WHEN c.status = 'HIEU_LUC' THEN 1 ELSE 0 END),
			SUM(CASE WHEN c.status = 'SAP_HET_HAN' THEN 1 ELSE 0 END),
			SUM(CASE WHEN c.status = 'HET_HAN' THEN 1 ELSE 0 END),
			SUM(CASE WHEN c.status = 'DA_THANH_LY' THEN 1 ELSE 0 END),
			SUM(CASE WHEN c.status = 'DA_HUY' THEN 1 ELSE 0 END)
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
            @Param("startOfMonth") LocalDateTime startOfMonth, @Param("endOfMonth") LocalDateTime endOfMonth);

	@Query("""
		SELECT c FROM Contract c
		WHERE c.room.id = :roomId
		AND c.status = 'HIEU_LUC'
	""")
	Optional<Contract> findActiveContractByRoomId(@Param("roomId") String roomId);


	boolean existsByRoomIdAndStatusIn(String roomId, List<ContractStatus> statuses);

    boolean existsByTenants_Id(String tenantId);

	@Query("""
    SELECT DISTINCT c
    FROM Contract c
    JOIN c.tenants t
    JOIN t.user u
    WHERE u.id = :userId
      AND (:query IS NULL OR LOWER(c.contractCode) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(c.room.roomCode) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(t.fullName) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(t.phoneNumber) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(t.identityCardNumber) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(t.email) LIKE LOWER(CONCAT('%', :query, '%')))
      AND (:gender IS NULL OR t.gender = :gender)
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
        JOIN c.tenants t
        WHERE t.id = :tenantId
    """)
	List<Contract> findAllByTenantId(@Param("tenantId") String tenantId);
}
