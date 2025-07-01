package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.dto.response.asset.CreateAssetInitResponse;
import com.example.datn_qlnt_manager.entity.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, String> {

    List<Asset> findByNameAssetIgnoreCase(String nameAsset);

    List<Asset> findByNameAssetIgnoreCaseAndIdNot(String nameAsset, String id);

    @Query("""
                SELECT a from Asset a
                JOIN a.building.user u
                WHERE (:nameAsset IS NULL OR a.nameAsset LIKE CONCAT('%', :nameAsset, '%'))
                AND u.id = :userId
            """)
    Page<Asset> searchAssets(@Param("nameAsset") String nameAsset, @Param("userId") String userId, Pageable pageable);

    // hiển thị theo userId
    @Query("""
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
