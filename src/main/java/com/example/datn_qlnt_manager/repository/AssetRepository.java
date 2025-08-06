package com.example.datn_qlnt_manager.repository;

import java.util.List;

import com.example.datn_qlnt_manager.common.AssetType;
import com.example.datn_qlnt_manager.dto.statistics.AssetStatusStatistic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.common.AssetBeLongTo;
import com.example.datn_qlnt_manager.common.AssetStatus;
import com.example.datn_qlnt_manager.entity.Asset;

@Repository
public interface AssetRepository extends JpaRepository<Asset, String> {

	List<Asset> findByNameAssetIgnoreCaseAndBuildingId(String nameAsset, String buildingId);

	List<Asset> findByNameAssetIgnoreCaseAndIdNot(String nameAsset, String id);

	@Query(
			"""
                SELECT a FROM Asset a
                JOIN a.building b
                WHERE a.building.id = :buildingId
                AND (:name IS NULL OR a.nameAsset LIKE CONCAT('%', :name, '%'))
                AND (:beLongTo IS NULL OR a.assetBeLongTo = :beLongTo)
                AND (:assetType IS NULL OR a.assetType = :assetType)
                AND (:assetStatus IS NULL OR a.assetStatus = :assetStatus)
            """)
	Page<Asset> findAllByFilterAndUserId(
			@Param("name") String nameAsset,
			@Param("assetType") AssetType assetType,
			@Param("beLongTo") AssetBeLongTo assetBeLongTo,
			@Param("assetStatus") AssetStatus assetStatus,
			@Param("buildingId") String buildingId,
			Pageable pageable);

	// hiển thị theo userId
	@Query(
			"""
                SELECT a FROM Asset a
                WHERE a.building.id = :buildingId
            """)
	List<Asset> findAssetsByBuildingId(@Param("buildingId") String buildingId);

	@Query("""
    SELECT new com.example.datn_qlnt_manager.dto.statistics.AssetStatusStatistic(
        COUNT(a),
        SUM(CASE WHEN a.assetStatus = 'HOAT_DONG' THEN 1 ELSE 0 END),
        SUM(CASE WHEN a.assetStatus = 'HU_HONG' THEN 1 ELSE 0 END),
        SUM(CASE WHEN a.assetStatus = 'CAN_BAO_TRI' THEN 1 ELSE 0 END),
        SUM(CASE WHEN a.assetStatus = 'THAT_LAC' THEN 1 ELSE 0 END),
        SUM(CASE WHEN a.assetStatus = 'KHONG_SU_DUNG' THEN 1 ELSE 0 END)
    )
    FROM Asset a
    WHERE a.building.id = :buildingId
""")
	AssetStatusStatistic getAssetStatisticsByBuildingId(@Param("buildingId") String buildingId);

	@Query("""
    SELECT a FROM Asset a
    WHERE a.assetStatus != com.example.datn_qlnt_manager.common.AssetStatus.HUY
""")
	List<Asset> findAllAssets();
}

