package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.entity.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, String> {

    List<Asset> findByNameAssetIgnoreCase(String nameAsset);

    List<Asset> findByNameAssetIgnoreCaseAndIdNot(String nameAsset, String id);

    @Query("""
    SELECT a from Asset a
    WHERE (:nameAsset IS NULL OR a.nameAsset LIKE CONCAT('%', :nameAsset, '%'))
""")
    Page<Asset> searchAssets(String nameAsset, Pageable pageable);
}
