package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.common.BuildingStatus;
import com.example.datn_qlnt_manager.common.ServiceAppliedBy;
import com.example.datn_qlnt_manager.common.ServiceStatus;
import com.example.datn_qlnt_manager.common.ServiceType;
import com.example.datn_qlnt_manager.dto.response.service.ServiceCountResponse;
import com.example.datn_qlnt_manager.entity.Building;
import com.example.datn_qlnt_manager.entity.Service;
import com.example.datn_qlnt_manager.entity.User;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, String> {


    @Query("""
                SELECT s
                FROM Service s
                WHERE (
                    :query IS NULL OR
                    LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR
                    LOWER(s.unit) LIKE LOWER(CONCAT('%', :query, '%'))
                )
                AND (:serviceType IS NULL OR s.type = :serviceType)
                AND (:userId IS NULL OR s.user.id = :userId)
                AND (:minPrice IS NULL OR s.price >= :minPrice)
                AND (:maxPrice IS NULL OR s.price <= :maxPrice)
                AND (:serviceStatus IS NULL OR s.status = :serviceStatus)
                AND (:serviceAppliedBy IS NULL OR s.appliedBy = :serviceAppliedBy)
            """)
    Page<Service> filterServicesPaging(
            @Param("userId") String userId,
            @Param("query") String query,
            @Param("serviceType") ServiceType serviceType,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("serviceStatus") ServiceStatus serviceStatus,
            @Param("serviceAppliedBy") ServiceAppliedBy serviceAppliedBy,
            Pageable pageable
    );

    @Query("""
                SELECT new com.example.datn_qlnt_manager.dto.response.service.ServiceCountResponse(
                    COUNT(s.id),
                    SUM(CASE WHEN s.status = 'HOAT_DONG' THEN 1 ELSE 0 END),
                    SUM(CASE WHEN s.status = 'KHONG_SU_DUNG' THEN 1 ELSE 0 END)
                )
                FROM Service s
                WHERE s.user.id = :userId
            """)
    ServiceCountResponse getServiceStats(@Param("userId") String userId);

    Optional<Service> findByIdAndStatusNot(String id, ServiceStatus status);

    List<Service> user(User user);
    // boolean existsByTenDichVu(String name);
}
