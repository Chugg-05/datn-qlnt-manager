package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.common.AssetGroup;
import com.example.datn_qlnt_manager.entity.AssetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetTypeRepository extends JpaRepository<AssetType, String> {
    @Query("""
    SELECT a FROM AssetType a
    WHERE (:name IS NULL OR a.nameAssetType LIKE CONCAT('%', :name, '%'))
      AND (:group IS NULL OR a.assetGroup = :group)
""")
    Page<AssetType> filterAssetTypesPaging(
            @Param("name") String nameAssetType,
            @Param("group") AssetGroup assetGroup,
            Pageable pageable
    );

    // check trùng tên loại trong nhóm
    boolean existsByNameAssetTypeAndAssetGroup(String name, AssetGroup group);

    Optional<AssetType> findByNameAssetTypeAndAssetGroupAndIdNot(String nameAssetType, AssetGroup assetGroup, String assetTypeId);


}
