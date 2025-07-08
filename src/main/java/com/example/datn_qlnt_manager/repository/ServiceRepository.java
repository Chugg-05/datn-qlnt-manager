package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.common.ServiceAppliedBy;
import com.example.datn_qlnt_manager.common.ServiceStatus;
import com.example.datn_qlnt_manager.common.ServiceType;
import com.example.datn_qlnt_manager.entity.Service;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ServiceRepository extends JpaRepository<Service, String> {

    @Query("""
                SELECT s
                FROM Service s
                WHERE (:query IS NULL OR s.name LIKE %:query%)
                  AND (:serviceType IS NULL OR s.type = :serviceType)
                  AND (:minPrice IS NULL OR s.price >= :minPrice)
                  AND (:maxPrice IS NULL OR s.price <= :maxPrice)
                  AND (:serviceStatus IS NULL OR s.status = :serviceStatus)
                  AND (:serviceAppliedBy IS NULL OR s.appliedBy = :serviceAppliedBy)
            """)
    Page<Service> filterServicesPaging(
            @Param("query") String query,
            @Param("serviceType") ServiceType serviceType,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("serviceStatus") ServiceStatus serviceStatus,
            @Param("serviceAppliedBy") ServiceAppliedBy serviceAppliedBy,
            Pageable pageable
    );


    // boolean existsByTenDichVu(String name);
}
