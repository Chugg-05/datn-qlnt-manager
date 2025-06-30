package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.common.ContractStatus;
import com.example.datn_qlnt_manager.common.Gender;
import com.example.datn_qlnt_manager.dto.response.contract.ContractDetailResponse;
import com.example.datn_qlnt_manager.dto.statistics.ContractStatistics;
import com.example.datn_qlnt_manager.entity.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
		AND (:gender IS NULL OR t.gender = :gender)
		AND (:status IS NULL OR c.status = :status)
		ORDER BY c.updatedAt DESC
	""")
    Page<Contract> filterContractPaging(
            @Param("userId") String userId,
            @Param("query") String query,
            @Param("gender") Gender gender,
            @Param("status") ContractStatus status,
            Pageable pageable);


	@Query("""
    SELECT new com.example.datn_qlnt_manager.dto.response.contract.ContractDetailResponse(
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
        r.price,
        b.address,
        c.status,
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


	@Query("""
		SELECT c FROM Contract c
		JOIN c.room r
		JOIN r.floor f
		JOIN f.building b
		JOIN b.user u
		WHERE u.id = :userId
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

	boolean existsByRoomIdAndStatusIn(String roomId, List<ContractStatus> statuses);

}
