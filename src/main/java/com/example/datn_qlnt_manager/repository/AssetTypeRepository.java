package com.example.datn_qlnt_manager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.common.AssetGroup;
import com.example.datn_qlnt_manager.entity.AssetType;

@Repository
public interface AssetTypeRepository extends JpaRepository<AssetType, String> {
    @Query(
            """
	SELECT a FROM AssetType a
	WHERE (:name IS NULL OR a.nameAssetType LIKE CONCAT('%', :name, '%'))
	AND (:group IS NULL OR a.assetGroup = :group)
	ORDER BY a.updatedAt DESC
	
""")
    Page<AssetType> filterAssetTypesPaging(
            @Param("name") String nameAssetType, @Param("group") AssetGroup assetGroup, Pageable pageable);


    @Query("""
        SELECT at
        FROM Asset a
        JOIN a.assetType at
        JOIN a.tenant t
        JOIN t.user u
        WHERE u.id = :userId
    """)
    List<AssetType>findAllLAssetTypeByUserId(@Param("userId") String userId);
    // check trùng tên loại trong nhóm
    boolean existsByNameAssetTypeAndAssetGroupAndUserId(String name, AssetGroup group, String userId);

    Optional<AssetType> findByNameAssetTypeAndAssetGroupAndIdNot(
            String nameAssetType, AssetGroup assetGroup, String assetTypeId);

    // hiển thị tài sản theo người dùng đang đăng nhập
    @Query("SELECT a FROM AssetType a WHERE a.user.id = :userId ORDER BY a.updatedAt DESC")
    List<AssetType> findAllByUserId(@Param("userId") String userId);
}
