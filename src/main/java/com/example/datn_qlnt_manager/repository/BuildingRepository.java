package com.example.datn_qlnt_manager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.common.BuildingStatus;
import com.example.datn_qlnt_manager.common.BuildingType;
import com.example.datn_qlnt_manager.dto.response.IdAndName;
import com.example.datn_qlnt_manager.dto.response.building.BuildingBasicResponse;
import com.example.datn_qlnt_manager.dto.response.building.BuildingOccupancyResponse;
import com.example.datn_qlnt_manager.dto.statistics.BuildingStatistics;
import com.example.datn_qlnt_manager.entity.Building;

@Repository
public interface BuildingRepository extends JpaRepository<Building, String> {

    @Query(
            """
				SELECT b FROM Building b
				WHERE (b.user.id = :userId)
				AND ((:query IS NULL OR b.buildingCode LIKE CONCAT('%', :query, '%') )
				OR (:query IS NULL OR  b.buildingName LIKE  CONCAT('%', :query, '%') )
				OR (:query IS NULL OR b.address LIKE CONCAT('%', :query, '%') ))
				AND (:status IS NULL OR  b.status = :status )
				AND (:buildingType IS NULL OR b.buildingType = :buildingType)
				AND b.status != 'HUY_HOAT_DONG'
				ORDER BY b.updatedAt DESC
			""")
    Page<Building> getPageAndSearchAndFilterBuildingByUserId(
            @Param("userId") String userId,
            @Param("query") String query,
            @Param("status") BuildingStatus status,
            @Param("buildingType") BuildingType buildingType,
            Pageable pageable);

    @Query(
            """
				SELECT b FROM Building b
				WHERE (b.user.id = :userId)
				AND ((:query IS NULL OR b.buildingCode LIKE CONCAT('%', :query, '%') )
				OR (:query IS NULL OR  b.buildingName LIKE  CONCAT('%', :query, '%') )
				OR (:query IS NULL OR b.address LIKE CONCAT('%', :query, '%') ))
				AND (:buildingType IS NULL OR b.buildingType = :buildingType)
				AND b.status = 'HUY_HOAT_DONG'
				ORDER BY b.updatedAt DESC
			""")
    Page<Building> getBuildingWithStatusCancelByUserId(
            @Param("userId") String userId,
            @Param("query") String query,
            @Param("buildingType") BuildingType buildingType,
            Pageable pageable);

    @Query(
            """
				SELECT new com.example.datn_qlnt_manager.dto.statistics.BuildingStatistics(
					COUNT(b.id),
					SUM(CASE WHEN b.status = 'HOAT_DONG' THEN 1 ELSE 0 END),
					SUM(CASE WHEN b.status = 'TAM_KHOA' THEN 1 ELSE 0 END)
				)
				FROM Building b
				WHERE b.user.id = :userId AND b.status != 'HUY_HOAT_DONG'
			""")
    BuildingStatistics getBuildingStatsByUser(@Param("userId") String userId);

    @Query(
            """
				SELECT new com.example.datn_qlnt_manager.dto.response.building.BuildingBasicResponse(
					b.id,
					b.buildingName,
					b.address,
					b.buildingType,
					b.status,
					SUM(CASE WHEN r.status = 'TRONG' THEN 1 ELSE 0 END),
					COUNT(r)
				)
				FROM Building b
				LEFT JOIN Floor f ON f.building.id = b.id
				LEFT JOIN Room r ON r.floor.id = f.id
				WHERE b.user.id = :userId AND b.status != 'HUY_HOAT_DONG'
				GROUP BY b.id, b.buildingName, b.address, b.buildingType, b.status
			""")
    List<BuildingBasicResponse> findAllBuildingBasicByUserId(@Param("userId") String userId);

    boolean existsByBuildingNameAndUserId(String buildingName, String userId); // check trùng tên khi thêm tòa nhà

    boolean existsByBuildingNameAndUserIdAndIdNot(
            String buildingName, String userId, String id); // check trùng tên khi update tòa nhà

    Optional<Building> findByIdAndStatusNot(String id, BuildingStatus status);

    @Query(
            """
				SELECT new com.example.datn_qlnt_manager.dto.response.IdAndName(b.id, b.buildingName)
				FROM Building b
				WHERE b.user.id = :userId AND b.status != com.example.datn_qlnt_manager.common.BuildingStatus.HUY_HOAT_DONG
			""")
    List<IdAndName> findAllBuildingsByUserId(@Param("userId") String userId);

    @Query(
            """
	SELECT new com.example.datn_qlnt_manager.dto.response.building.BuildingOccupancyResponse(
		b.id,
		b.buildingName,
		COUNT(DISTINCT r.id),
		SUM(CASE
				WHEN c.status = 'HIEU_LUC' OR c.status = 'SAP_HET_HAN' THEN 1
				ELSE 0
			END)
	)
	FROM Contract c
	JOIN c.room r
	JOIN r.floor f
	JOIN f.building b
	WHERE b.user.id = :userId
	GROUP BY b.id, b.buildingName
""")
    List<BuildingOccupancyResponse> calculateBuildingOccupancy(@Param("userId") String userId);
}
