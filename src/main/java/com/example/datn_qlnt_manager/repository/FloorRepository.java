package com.example.datn_qlnt_manager.repository;

import java.util.List;
import java.util.Optional;

import com.example.datn_qlnt_manager.dto.statistics.FloorRoomStatisticResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.common.FloorStatus;
import com.example.datn_qlnt_manager.common.FloorType;
import com.example.datn_qlnt_manager.dto.response.IdAndName;
import com.example.datn_qlnt_manager.dto.response.floor.FloorBasicResponse;
import com.example.datn_qlnt_manager.dto.statistics.FloorStatistics;
import com.example.datn_qlnt_manager.entity.Floor;

@Repository
public interface FloorRepository extends JpaRepository<Floor, String> {

    // hiển thị, lọc, tìm kiếm
    @Query(
            """
				SELECT f FROM Floor f
				JOIN f.building b
				WHERE (:buildingId IS NULL OR b.id = :buildingId)
				AND (:status IS NULL OR f.status = :status)
				AND (:nameFloor IS NULL OR f.nameFloor LIKE CONCAT('%', :nameFloor, '%'))
				AND (:maxRoom IS NULL OR f.maximumRoom =: maxRoom)
				AND (:floorType IS NULL OR f.floorType = :floorType)
				AND f.status != 'KHONG_SU_DUNG'
				AND f.building.user.id = :userId
				ORDER BY f.updatedAt DESC
			""")
    Page<Floor> getPageAndSearchAndFilterFloorByUserId(
            @Param("userId") String userId,
            @Param("buildingId") String buildingId,
            @Param("status") FloorStatus status,
            @Param("floorType") FloorType floorType,
            @Param("nameFloor") String nameFloor,
            @Param("maxRoom") Integer maxRoom,
            Pageable pageable);

    @Query(
            """
				SELECT f FROM Floor f
				JOIN f.building b
				WHERE (:buildingId IS NULL OR b.id = :buildingId)
				AND (:nameFloor IS NULL OR f.nameFloor LIKE CONCAT('%', :nameFloor, '%'))
				AND (:maxRoom IS NULL OR f.maximumRoom =: maxRoom)
				AND (:floorType IS NULL OR f.floorType = :floorType)
				AND f.status = 'KHONG_SU_DUNG'
				AND f.building.user.id = :userId
				ORDER BY f.updatedAt DESC
			""")
    Page<Floor> getFloorWithStatusCancelByUserId(
            @Param("userId") String userId,
            @Param("buildingId") String buildingId,
            @Param("floorType") FloorType floorType,
            @Param("nameFloor") String nameFloor,
            @Param("maxRoom") Integer maxRoom,
            Pageable pageable);

    Optional<Floor> findByNameFloorAndBuilding_Id(String nameFloor, String buildingId);

    // thông kê
    @Query(
            """
				SELECT new com.example.datn_qlnt_manager.dto.statistics.FloorStatistics(
					:buildingId,
					COUNT(f.id),
					SUM(CASE WHEN f.status = 'HOAT_DONG' THEN 1 ELSE 0 END),
					SUM(CASE WHEN f.status = 'TAM_KHOA' THEN 1 ELSE 0 END),
					SUM(CASE WHEN f.status = 'KHONG_SU_DUNG' THEN 1 ELSE 0 END)
				)
				FROM Floor f
				WHERE f.building.id = :buildingId
			""")
    FloorStatistics countFloorsByBuildingId(@Param("buildingId") String buildingId);

    @Query(
            """
				SELECT new com.example.datn_qlnt_manager.dto.response.floor.FloorBasicResponse(
					f.id,
					f.nameFloor,
					f.floorType,
					f.status,
					f.maximumRoom,
					b.buildingName
				)
				FROM Floor f
				JOIN f.building b
				WHERE b.id = :buildingId
				ORDER BY f.updatedAt DESC
			""")
    List<FloorBasicResponse> findAllFloorBasicByBuildingId(@Param("buildingId") String buildingId);

    @Query("SELECT f.nameFloor FROM Floor f WHERE f.building.id = :buildingId")
    List<String> findAllNamesByBuildingId(@Param("buildingId") String buildingId);

    Optional<Floor> findByIdAndStatusNot(String id, FloorStatus status);

    @Query(
            """
				SELECT new com.example.datn_qlnt_manager.dto.response.IdAndName(f.id, f.nameFloor)
				FROM Floor f
				JOIN f.building b
				WHERE b.user.id = :userId AND f.status != 'KHONG_SU_DUNG' AND b.id = :buildingId
			""")
    List<IdAndName> findAllFloorsByUserIdAndBuildingId(
            @Param("userId") String userId, @Param("buildingId") String buildingId);

    @Query(
            """
				SELECT new com.example.datn_qlnt_manager.dto.response.IdAndName(
					f.id, CONCAT(b.buildingName, ' - ', f.nameFloor)
				)
				FROM Floor f
				JOIN f.building b
				WHERE b.user.id = :userId AND f.status != 'KHONG_SU_DUNG'
			""")
    List<IdAndName> getFloorsByUserId(@Param("userId") String userId);

    @Query("SELECT COUNT(f) FROM Floor f WHERE f.building.id = :buildingId")
    int countByBuildingId(@Param("buildingId") String buildingId);


	@Query("""
    SELECT new com.example.datn_qlnt_manager.dto.statistics.FloorRoomStatisticResponse(
        f.id,
        CONCAT(f.nameFloor, ' - Trống ',
               SUM(CASE WHEN r.status = 'TRONG' THEN 1 ELSE 0 END), '/', COUNT(r.id))
    )
    FROM Floor f
    JOIN Room r ON r.floor.id = f.id
    WHERE f.id = :floorId
    GROUP BY f.id, f.nameFloor
""")
	List<FloorRoomStatisticResponse> getRoomStatisticTextByFloor(@Param("floorId") String floorId);
}
