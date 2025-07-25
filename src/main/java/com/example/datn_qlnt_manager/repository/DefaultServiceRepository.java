package com.example.datn_qlnt_manager.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.common.DefaultServiceAppliesTo;
import com.example.datn_qlnt_manager.common.DefaultServiceStatus;
import com.example.datn_qlnt_manager.dto.statistics.DefaultServiceStatistics;
import com.example.datn_qlnt_manager.entity.DefaultService;

@Repository
public interface DefaultServiceRepository extends JpaRepository<DefaultService, String> {

    @Query(
            """
				SELECT ds FROM DefaultService ds
				INNER JOIN ds.building b
				INNER JOIN ds.floor f
				INNER JOIN ds.service s
				WHERE (ds.building.user.id = :userId)
				AND (:buildingId IS NULL OR b.id = :buildingId)
				AND (:floorId IS NULL OR f.id = :floorId)
				AND (:serviceId IS NULL OR s.id = :serviceId)
				AND (:status IS NULL OR ds.defaultServiceStatus = :status)
				AND (:appliesTo IS NULL OR ds.defaultServiceAppliesTo = :appliesTo)
				AND (:maxPricesApply IS NULL OR ds.pricesApply <= :maxPricesApply)
				AND (:minPricesApply IS NULL OR ds.pricesApply >= :minPricesApply)
				AND ds.defaultServiceStatus != 'HUY_BO'
				ORDER BY ds.updatedAt DESC
			""")
    Page<DefaultService> getPageAndSearchAndFilterDefaultServiceByUserId(
            @Param("userId") String userId,
            @Param("buildingId") String buildingId,
            @Param("floorId") String floorId,
            @Param("serviceId") String serviceId,
            @Param("status") DefaultServiceStatus defaultServiceStatus,
            @Param("appliesTo") DefaultServiceAppliesTo defaultServiceAppliesTo,
            @Param("maxPricesApply") BigDecimal maxPricesApply,
            @Param("minPricesApply") BigDecimal minPricesApply,
            Pageable pageable);

    @Query(
            """
				SELECT ds FROM DefaultService ds
				INNER JOIN ds.building b
				INNER JOIN ds.floor f
				INNER JOIN ds.service s
				WHERE (ds.building.user.id = :userId)
				AND (:buildingId IS NULL OR b.id = :buildingId)
				AND (:floorId IS NULL OR f.id = :floorId)
				AND (:serviceId IS NULL OR s.id = :serviceId)
				AND (:appliesTo IS NULL OR ds.defaultServiceAppliesTo = :appliesTo)
				AND (:maxPricesApply IS NULL OR ds.pricesApply <= :maxPricesApply)
				AND (:minPricesApply IS NULL OR ds.pricesApply >= :minPricesApply)
				AND ds.defaultServiceStatus != 'HUY_BO'
				ORDER BY ds.updatedAt DESC
			""")
    Page<DefaultService> getDefaultServiceWithStatusCancelByUserId(
            @Param("userId") String userId,
            @Param("buildingId") String buildingId,
            @Param("floorId") String floorId,
            @Param("serviceId") String serviceId,
            @Param("appliesTo") DefaultServiceAppliesTo defaultServiceAppliesTo,
            @Param("maxPricesApply") BigDecimal maxPricesApply,
            @Param("minPricesApply") BigDecimal minPricesApply,
            Pageable pageable);

    boolean existsByBuildingIdAndServiceIdAndDefaultServiceAppliesTo(
            String buildingId, String serviceId, DefaultServiceAppliesTo defaultServiceAppliesTo);

    Optional<DefaultService> findByIdAndDefaultServiceStatusNot(String id, DefaultServiceStatus defaultServiceStatus);

    @Query(
            """
			SELECT new com.example.datn_qlnt_manager.dto.statistics.DefaultServiceStatistics(
			COUNT(ds),
			SUM(CASE WHEN ds.defaultServiceStatus = 'HOAT_DONG' THEN 1 ELSE 0 END),
			SUM(CASE WHEN ds.defaultServiceStatus = 'TAM_DUNG' THEN 1 ELSE 0 END)
			)
			FROM DefaultService ds
			WHERE ds.service.user.id = :userId
			""")
    DefaultServiceStatistics statisticsDefaultServiceByStatusWhereUserId(@Param("userId") String userId);
}
