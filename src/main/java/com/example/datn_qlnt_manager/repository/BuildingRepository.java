package com.example.datn_qlnt_manager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.common.BuildingStatus;
import com.example.datn_qlnt_manager.entity.Building;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface BuildingRepository extends JpaRepository<Building, String> {
	boolean existsByBuildingCode(String buildingCode); // kiểm tra mã tòa đã tồn tại

	@Query(
			"""
        SELECT b FROM Building b
        INNER JOIN b.user u
        WHERE (u.id = :userId)
        AND (:buildingCode IS NULL OR b.buildingCode LIKE CONCAT('%', :buildingCode, '%') )
        AND (:buildingName IS NULL OR  b.buildingName LIKE  CONCAT('%', :buildingName, '%') )
        AND (:address IS NULL OR b.address LIKE CONCAT('%', :address, '%') )
        AND (:status IS NULL OR  b.status = :status )
    """)
	Page<Building> filterBuildingPaging(
			@Param("userId") String userId,
			@Param("buildingCode") String buildingCode,
			@Param("buildingName") String buildingName,
			@Param("address") String address,
			@Param("status") BuildingStatus status,
			Pageable pageable);

	boolean existsByBuildingNameAndUserId(String buildingName, String userId); // check trùng tên khi thêm tòa nhà

	boolean existsByBuildingNameAndUserIdAndIdNot(
			String buildingName, String userId, String id); // check trùng tên khi update tòa nhà
}