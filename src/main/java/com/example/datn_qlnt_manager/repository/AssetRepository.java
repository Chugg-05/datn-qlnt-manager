package com.example.datn_qlnt_manager.repository;

import java.util.List;

import com.example.datn_qlnt_manager.common.AssetType;
import jakarta.persistence.AccessType;
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
				WHERE a.user.id = :userId
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
            @Param("userId") String userId,
            Pageable pageable);

    // hiển thị theo userId
    @Query(
            """
				SELECT a FROM Asset a
				WHERE a.user.id = :userId
			""")
    List<Asset> findAssetsByUserId(@Param("userId") String userId);
}
