package com.example.datn_qlnt_manager.repository;

import java.util.Optional;

import com.example.datn_qlnt_manager.common.FloorStatus;

import com.example.datn_qlnt_manager.dto.response.floor.FloorCountResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.entity.Floor;

@Repository
public interface FloorRepository extends JpaRepository<Floor, String> {

// hiển thị, lọc, tìm kiếm
@Query("""
    SELECT f FROM Floor f
     JOIN f.building b
    WHERE (:buildingId IS NULL OR b.id = :buildingId)
      AND (:status IS NULL OR f.status = :status)
      AND (:nameFloor IS NULL OR f.nameFloor LIKE CONCAT('%', :nameFloor, '%'))
      AND (:maxRoom IS NULL OR f.maximumRoom =: maxRoom)
""")
Page<Floor> filterFloorsPaging(
        @Param("buildingId") String buildingId,
        @Param("status") FloorStatus status,
        @Param("nameFloor") String nameFloor,
        @Param("maxRoom") Integer maxRoom,
        Pageable pageable
);

Optional<Floor> findByNameFloorAndBuilding_Id(String nameFloor, String buildingId);

Optional<Floor> findByNameFloorAndBuilding_IdAndIdNot(String nameFloor, String buildingId, String excludedId);

    // thông kê
    @Query("""
    SELECT new com.example.datn_qlnt_manager.dto.response.floor.FloorCountResponse(
        :buildingId,
        COUNT(f.id),
        SUM(CASE WHEN f.status = 'HOAT_DONG' THEN 1 ELSE 0 END),
        SUM(CASE WHEN f.status = 'KHONG_SU_DUNG' THEN 1 ELSE 0 END)
    )
    FROM Floor f
    WHERE f.building.id = :buildingId
""")
    FloorCountResponse countFloorsByBuildingId(@Param("buildingId") String buildingId);
}
