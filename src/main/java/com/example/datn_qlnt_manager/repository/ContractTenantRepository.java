package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.common.ContractStatus;
import com.example.datn_qlnt_manager.common.Gender;
import com.example.datn_qlnt_manager.dto.response.contractTenant.ContractTenantDetailResponse;
import com.example.datn_qlnt_manager.dto.response.tenant.TenantLittleResponse;
import com.example.datn_qlnt_manager.entity.ContractTenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractTenantRepository extends JpaRepository<ContractTenant, String> {

    @Query("""
        SELECT new com.example.datn_qlnt_manager.dto.response.contractTenant.ContractTenantDetailResponse(
        ct.id,
        c.id,
        t.id,
        t.customerCode,
        t.fullName,
        t.gender,
        t.phoneNumber,
        t.email,
        ct.representative,
        ct.startDate,
        ct.endDate,
        ct.createdAt,
        ct.updatedAt
        )
        FROM ContractTenant ct
        JOIN ct.tenant t
        JOIN ct.contract c
        WHERE ct.contract.id = :contractId
        AND ((:query IS NULL OR t.customerCode LIKE CONCAT('%', :query, '%') )
        OR (:query IS NULL OR  t.fullName LIKE  CONCAT('%', :query, '%') )
        OR (:query IS NULL OR  t.phoneNumber LIKE  CONCAT('%', :query, '%') )
        OR (:query IS NULL OR  t.email LIKE CONCAT('%', :query, '%') ))
        AND (:gender IS NULL OR t.gender = :gender)
        ORDER BY ct.representative DESC, c.updatedAt DESC
        """)
    Page<ContractTenantDetailResponse> findAllTenantsByContractId(
            @Param("contractId") String contractId,
            @Param("query") String query,
            @Param("gender") Gender gender,
            Pageable pageable);

    @Query("""
        SELECT new com.example.datn_qlnt_manager.dto.response.tenant.TenantLittleResponse(
           ct.id,
           t.id,
           t.customerCode,
           t.fullName,
           t.gender,
           t.phoneNumber,
           t.email,
           ct.representative
        )
        FROM ContractTenant ct
        JOIN ct.tenant t
        WHERE ct.contract.id = :contractId
    """)
    List<TenantLittleResponse> findAllTenantLittleResponseByContractId(@Param("contractId") String contractId);

    List<ContractTenant> findAllByContractIdAndRepresentativeTrue(String contractId);

    Optional<ContractTenant> findByContractIdAndTenantId(String contractId, String tenantId);

    Optional<ContractTenant> findByContractIdAndRepresentativeTrue(String contractId);

    List<ContractTenant> findByContractId(String contractId);

    boolean existsByTenantIdAndContract_StatusIn(String tenantId, List<ContractStatus> statuses);

    boolean existsByTenantIdAndRepresentativeTrueAndContractIdNot(String tenantId, String contractId);

    boolean existsByTenantIdAndContractStatusIn(String tenantId, List<ContractStatus> statuses);

}
