package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.dto.response.tenant.TenantBasicResponse;
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

@Repository
public interface TenantRepository extends JpaRepository<Tenant, String> {
    @Query(
            """
		SELECT t FROM Tenant t
		INNER JOIN t.user u
		WHERE (u.id = :userId)
		AND (:customerCode IS NULL OR t.customerCode LIKE CONCAT('%', :customerCode, '%'))
		AND (:fullName IS NULL OR t.fullName LIKE CONCAT('%', :fullName, '%'))
		AND (:email IS NULL OR t.email LIKE CONCAT('%', :email, '%'))
		AND (:phoneNumber IS NULL OR t.phoneNumber LIKE CONCAT('%', :phoneNumber, '%'))
		AND (:identityCardNumber IS NULL OR t.identityCardNumber LIKE CONCAT('%', :identityCardNumber, '%'))
		AND (:address IS NULL OR t.address LIKE CONCAT('%', :address, '%'))
		AND (:gender IS NULL OR t.gender = :gender)
		AND (:tenantStatus IS NULL OR t.tenantStatus = :tenantStatus)
		""")
    Page<Tenant> filterTenantPaging(
            @Param("userId") String userId,
            @Param("customerCode") String customerCode,
            @Param("fullName") String fullName,
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber,
            @Param("identityCardNumber") String identityCardNumber,
            @Param("address") String address,
            @Param("gender") Gender gender,
            @Param("tenantStatus") TenantStatus tenantStatus,
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
        WHERE t.user.id = :userId
    """)
	TenantStatistics getTotalTenantByStatus(@Param("userId") String userId);

	@Query("""
		SELECT new com.example.datn_qlnt_manager.dto.response.tenant.TenantBasicResponse(
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
		SELECT t FROM Tenant t
		WHERE t.user.id = :userId
		ORDER BY t.updatedAt DESC
	""")
	List<Tenant> findAllTenantsByUserId(@Param("userId") String userId);

	boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByIdentityCardNumber(String identityCardNumber);
}
