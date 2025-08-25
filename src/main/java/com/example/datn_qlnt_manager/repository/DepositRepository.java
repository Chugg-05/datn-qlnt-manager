package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.common.DepositStatus;
import com.example.datn_qlnt_manager.dto.projection.DepositDetailView;
import com.example.datn_qlnt_manager.entity.Deposit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, String> {
    @Query("""
        SELECT new com.example.datn_qlnt_manager.dto.projection.DepositDetailView(
            d.id,
            c.id,
            c.contractCode,
            r.roomCode,
            d.depositor,
            d.depositRecipient,
            d.depositAmount,
            d.depositStatus,
            d.refundAmount,
            d.depositDate,
            d.depositRefundDate,
            d.securityDepositReturnDate,
            d.note,
            d.createdAt,
            d.updatedAt
        )
        FROM Deposit d
        JOIN d.contract c
        JOIN c.room r
        WHERE r.floor.building.user.id = :userId
        AND ((:query IS NULL OR c.contractCode LIKE CONCAT('%', :query, '%') )
        OR (:query IS NULL OR  r.roomCode LIKE  CONCAT('%', :query, '%') )
        OR (:query IS NULL OR  d.depositor LIKE  CONCAT('%', :query, '%') )
        OR (:query IS NULL OR  d.depositRecipient LIKE CONCAT('%', :query, '%') ))
        AND (:building IS NULL OR r.floor.building.id = :building)
        AND (:floor IS NULL OR r.floor.id = :floor)
        AND (:room IS NULL OR r.id = :room)
        AND (:depositStatus IS NULL OR d.depositStatus = :depositStatus)
        ORDER BY d.updatedAt DESC
    """)
    Page<DepositDetailView> findAllDepositByUserId(
            @Param("userId") String userId,
            @Param("query") String query,
            @Param("building") String building,
            @Param("floor") String floor,
            @Param("room") String room,
            @Param("depositStatus") DepositStatus depositStatus,
            Pageable pageable
    );

    boolean existsByContractId(String contractId);
}
