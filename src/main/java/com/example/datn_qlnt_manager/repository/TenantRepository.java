package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.dto.response.IdAndName;
import com.example.datn_qlnt_manager.dto.response.tenant.TenantBasicResponse;
import com.example.datn_qlnt_manager.dto.response.tenant.TenantDetailResponse;
import com.example.datn_qlnt_manager.dto.response.tenant.TenantSelectResponse;
import com.example.datn_qlnt_manager.dto.statistics.TenantStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.common.Gender;
import com.example.datn_qlnt_manager.common.TenantStatus;
import com.example.datn_qlnt_manager.entity.Tenant;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, String> {
    @Query(
            """
                    SELECT t FROM Tenant t
                    WHERE (t.owner.id = :userId)
                    AND (:query IS NULL OR t.customerCode LIKE CONCAT('%', :query, '%'))
                    AND (:query IS NULL OR t.fullName LIKE CONCAT('%', :query, '%'))
                    AND (:query IS NULL OR t.email LIKE CONCAT('%', :query, '%'))
                    AND (:query IS NULL OR t.phoneNumber LIKE CONCAT('%', :query, '%'))
                    AND (:query IS NULL OR t.identityCardNumber LIKE CONCAT('%', :query, '%'))
                    AND (:query IS NULL OR t.address LIKE CONCAT('%', :query, '%'))
                    AND (:gender IS NULL OR t.gender = :gender)
                    AND (:tenantStatus IS NULL OR t.tenantStatus = :tenantStatus)
                    AND t.tenantStatus != 'HUY_BO'
                    ORDER BY t.updatedAt DESC
                    """)
    Page<Tenant> getPageAndSearchAndFilterTenantByUserId(
            @Param("userId") String userId,
            @Param("query") String query,
            @Param("gender") Gender gender,
            @Param("tenantStatus") TenantStatus tenantStatus,
            Pageable pageable);

    @Query(
            """
                    SELECT t FROM Tenant t
                    WHERE (t.owner.id = :userId)
                    AND (:query IS NULL OR t.customerCode LIKE CONCAT('%', :query, '%'))
                    AND (:query IS NULL OR t.fullName LIKE CONCAT('%', :query, '%'))
                    AND (:query IS NULL OR t.email LIKE CONCAT('%', :query, '%'))
                    AND (:query IS NULL OR t.phoneNumber LIKE CONCAT('%', :query, '%'))
                    AND (:query IS NULL OR t.identityCardNumber LIKE CONCAT('%', :query, '%'))
                    AND (:query IS NULL OR t.address LIKE CONCAT('%', :query, '%'))
                    AND (:gender IS NULL OR t.gender = :gender)
                    AND t.tenantStatus = 'HUY_BO'
                    ORDER BY t.updatedAt DESC
                    """)
    Page<Tenant> getTenantWithStatusCancelByUserId(
            @Param("userId") String userId,
            @Param("query") String query,
            @Param("gender") Gender gender,
            Pageable pageable);

    @Query(
            """
                        SELECT COUNT(t),
                               SUM(CASE WHEN t.tenantStatus = 'DANG_THUE' THEN 1 ELSE 0 END),
                               SUM(CASE WHEN t.tenantStatus = 'DA_TRA_PHONG' THEN 1 ELSE 0 END),
                               SUM(CASE WHEN t.tenantStatus = 'TIEM_NANG' THEN 1 ELSE 0 END),
                               SUM(CASE WHEN t.tenantStatus = 'HUY_BO' THEN 1 ELSE 0 END),
                               SUM(CASE WHEN t.tenantStatus = 'KHOA' THEN 1 ELSE 0 END)
                        FROM Tenant t
                        WHERE t.owner.id = :userId
                    """)
    TenantStatistics getTotalTenantByStatus(@Param("userId") String userId);

    @Query("""
            	SELECT new com.example.datn_qlnt_manager.dto.response.tenant.TenantBasicResponse(
                    t.id,
            		t.customerCode,
            		t.fullName,
            		t.email,
            		t.phoneNumber,
            		t.isRepresentative
            	)
            	FROM Contract c
            	JOIN c.tenants t
            	WHERE c.id = :contractId
            """)
    List<TenantBasicResponse> findTenantsByContractId(@Param("contractId") String contractId);

    @Query("""
            SELECT new com.example.datn_qlnt_manager.dto.response.tenant.TenantDetailResponse(
                t.id,
                t.customerCode,
                b.buildingName,
                r.roomCode,
                t.fullName,
                t.gender,
                t.dob,
                t.email,
                t.phoneNumber,
                t.identityCardNumber,
                t.address,
                c.contractCode,
                c.endDate,
                t.tenantStatus,
                t.isRepresentative,
                t.createdAt,
                t.updatedAt
            )
            FROM Tenant t
            LEFT JOIN t.contracts c
            LEFT JOIN c.room r
            LEFT JOIN r.floor f
            LEFT JOIN f.building b
            WHERE t.id = :tenantId
            """)
    Optional<TenantDetailResponse> findTenantDetailById(@Param("tenantId") String tenantId);


    @Query("""
            	SELECT t FROM Tenant t
            	WHERE t.owner.id = :userId
            	ORDER BY t.updatedAt DESC
            """)
    List<Tenant> findAllTenantsByUserId(@Param("userId") String userId);

    Optional<Tenant> findByUserId(String userId);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByIdentityCardNumber(String identityCardNumber);

    @Query("""
                SELECT new com.example.datn_qlnt_manager.dto.response.tenant.TenantSelectResponse(t.id, t.fullName)
                FROM Tenant t
                WHERE t.owner.id = :userId
            """)
    List<TenantSelectResponse> findAllTenantsByOwnerIdAndRoomId(@Param("userId") String userId, @Param("roomId") String roomId );
}
