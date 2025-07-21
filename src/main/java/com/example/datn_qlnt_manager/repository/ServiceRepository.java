package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.common.ServiceCalculation;
import com.example.datn_qlnt_manager.common.ServiceStatus;
import com.example.datn_qlnt_manager.common.ServiceCategory;
import com.example.datn_qlnt_manager.dto.response.IdAndName;
import com.example.datn_qlnt_manager.dto.response.service.ServiceCountResponse;
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
                AND (:category IS NULL OR s.serviceCategory = :category)
                AND (:userId IS NULL OR s.user.id = :userId)
                AND (:minPrice IS NULL OR s.price >= :minPrice)
                AND (:maxPrice IS NULL OR s.price <= :maxPrice)
                AND (:serviceStatus IS NULL OR s.status = :serviceStatus)
                AND (:serviceCalculation IS NULL OR s.serviceCalculation = :serviceCalculation)
            """)
    Page<Service> filterServicesPaging(
            @Param("userId") String userId,
            @Param("query") String query,
            @Param("category") ServiceCategory category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("serviceStatus") ServiceStatus serviceStatus,
            @Param("serviceCalculation") ServiceCalculation serviceCalculation,
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

    @Query("""
            SELECT new com.example.datn_qlnt_manager.dto.response.IdAndName(
            s.id,
            CONCAT(s.name,' - ',CAST(s.price as string), ' VNĐ')
            )
            FROM Service s
            WHERE s.user.id = :userId AND s.status != 'KHONG_SU_DUNG'
            """)
    List<IdAndName> findAllByUserId(String userId);

    @Query("""
            SELECT new com.example.datn_qlnt_manager.dto.response.IdAndName(
                s.id,
                s.name
            )
            FROM Service s
            WHERE s.user.id = :userId AND s.status != 'KHONG_SU_DUNG'
            """)
    List<IdAndName> getServiceInfoByUserId(@Param("userId") String userId);

    boolean existsByServiceCategory(ServiceCategory serviceCategory);

}
