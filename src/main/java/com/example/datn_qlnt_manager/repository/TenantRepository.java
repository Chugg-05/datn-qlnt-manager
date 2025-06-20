package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.common.Gender;
import com.example.datn_qlnt_manager.common.TenantStatus;
import com.example.datn_qlnt_manager.entity.Tenant;
import com.example.datn_qlnt_manager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, String> {
    @Query(
            """
		SELECT t FROM Tenant t
		LEFT JOIN t.user u
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
			@Param("tenantStatus")TenantStatus tenantStatus,
			Pageable pageable);

    boolean existsByUserId(String userId);
    boolean existsByCustomerCode(String customerCode);
    boolean existsByFullName(String fullName);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByIdentityCardNumber(String identityCardNumber);

	Optional<Tenant> findByUser(User user);
	Optional<Tenant> findByCustomerCode(String customerCode);
	Optional<Tenant> findByFullName(String fullName);
    Optional<Tenant> findByEmail(String email);
    Optional<Tenant> findByPhoneNumber(String phoneNumber);
    Optional<Tenant> findByIdentityCardNumber(String identityCardNumber);
}
