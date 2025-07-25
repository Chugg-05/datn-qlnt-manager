package com.example.datn_qlnt_manager.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.entity.ServicePriceHistory;

@Repository
public interface ServicePriceHistoryRepository extends JpaRepository<ServicePriceHistory, String> {
    @Query(
            """
	SELECT l FROM ServicePriceHistory l
	WHERE l.service.user.id = :userId
	AND (:serviceName IS NULL OR LOWER(l.service.name) LIKE LOWER(CONCAT('%', :serviceName, '%')))
	AND (:minOldPrice IS NULL OR l.oldPrice >= :minOldPrice)
	AND (:maxOldPrice IS NULL OR l.oldPrice <= :maxOldPrice)
	AND (:minNewPrice IS NULL OR l.newPrice >= :minNewPrice)
	AND (:maxNewPrice IS NULL OR l.newPrice <= :maxNewPrice)
	AND (:startDate IS NULL OR l.applicableDate >= :startDate)
	AND (:endDate IS NULL OR l.applicableDate <= :endDate)
	ORDER BY l.applicableDate DESC
""")
    Page<ServicePriceHistory> filterByUser(
            @Param("userId") String userId,
            @Param("serviceName") String serviceName,
            @Param("minOldPrice") BigDecimal minOldPrice,
            @Param("maxOldPrice") BigDecimal maxOldPrice,
            @Param("minNewPrice") BigDecimal minNewPrice,
            @Param("maxNewPrice") BigDecimal maxNewPrice,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}
