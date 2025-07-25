package com.example.datn_qlnt_manager.repository;

import java.util.List;

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

    List<Asset> findByNameAssetIgnoreCase(String nameAsset);

    List<Asset> findByNameAssetIgnoreCaseAndIdNot(String nameAsset, String id);

    @Query(
            """
				SELECT a FROM Asset a
				LEFT JOIN a.room r
				LEFT JOIN r.floor rf
				LEFT JOIN a.floor f
				LEFT JOIN f.building fb
				LEFT JOIN a.building b
				LEFT JOIN b.user ub
				LEFT JOIN fb.user ufb
				LEFT JOIN a.tenant t
				LEFT JOIN t.owner towner
				WHERE (
					(a.assetBeLongTo = 'CHUNG' AND (ub.id = :userId OR ufb.id = :userId))
					OR (a.assetBeLongTo = 'PHONG' AND ufb.id = :userId)
					OR (a.assetBeLongTo = 'CA_NHAN' AND towner.id = :userId)
				)
				AND (:name IS NULL OR a.nameAsset LIKE CONCAT('%', :name, '%'))
				AND (:beLongTo IS NULL OR a.assetBeLongTo = :beLongTo)
				AND (:status IS NULL OR a.assetStatus = :status)
			""")
    Page<Asset> findAllByFilterAndUserId(
            @Param("name") String nameAsset,
            @Param("beLongTo") AssetBeLongTo assetBeLongTo,
            @Param("status") AssetStatus assetStatus,
            @Param("userId") String userId,
            Pageable pageable);

    // hiển thị theo userId
    @Query(
            """
				SELECT a
				FROM Asset a
				LEFT JOIN a.room r
				LEFT JOIN r.floor f1
				LEFT JOIN f1.building b1
				LEFT JOIN a.floor f2
				LEFT JOIN f2.building b2
				LEFT JOIN a.building b3
				LEFT JOIN a.tenant t
				WHERE
					(r IS NOT NULL AND b1.user.id = :userId)
					OR (f2 IS NOT NULL AND b2.user.id = :userId)
					OR (b3 IS NOT NULL AND b3.user.id = :userId)
					OR (t IS NOT NULL AND t.user.id = :userId)
			""")
    List<Asset> findAssetsByUserId(@Param("userId") String userId);
}
