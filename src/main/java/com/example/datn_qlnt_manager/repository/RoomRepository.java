package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.dto.response.room.RoomCountResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.entity.Room;

import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {

    @Query(
            """
					SELECT r
					FROM Room r
					INNER JOIN Floor f ON r.floor = f
					WHERE (:status IS NULL OR r.status = :status)
					AND (:maxPrice IS NULL OR r.price <= :maxPrice)
					AND (:minPrice IS NULL OR r.price >= :minPrice)
					AND (:maxAcreage IS NULL OR r.acreage <= :maxAcreage)
					AND (:minAcreage IS NULL OR r.acreage >= :minAcreage)
					AND (:maxPerson IS NULL OR r.maximumPeople <= :maxPerson)
					AND (:nameFloor IS NULL OR f.nameFloor LIKE CONCAT('%', :nameFloor, '%'))
					""")
    Page<Room> filterRoomsPaging(
            @Param("status") String status,
            @Param("maxPrice") Double maxPrice,
            @Param("minPrice") Double minPrice,
            @Param("maxAcreage") Double maxAcreage,
            @Param("minAcreage") Double minAcreage,
            @Param("maxPerson") Integer maxPerson,
            @Param("nameFloor") String nameFloor,
            Pageable pageable);

    boolean existsByRoomCode(String roomCode);

	@Query("""
			SELECT 
				COUNT(CASE WHEN r.status IN (
					com.example.datn_qlnt_manager.common.RoomStatus.DANG_THUE,
					com.example.datn_qlnt_manager.common.RoomStatus.DA_DAT_COC
				) THEN 1 END),
				SUM(CASE WHEN r.status = 'DANG_THUE' THEN 1 ELSE 0 END),
				SUM(CASE WHEN r.status = 'DA_DAT_COC' THEN 1 ELSE 0 END)
			FROM Room r
			WHERE r.floor.id = :floorId
		""")
	RoomCountResponse getRoomStatsByFloor(@Param("floorId") String floorId);


	@Query("SELECT r.roomCode FROM Room r WHERE r.floor.building.id = :buildingId AND r.floor.id = :floorId")
	List<String> findRoomCodesByBuildingAndFloor(@Param("buildingId") String buildingId,
												 @Param("floorId") String floorId);

	int countByFloorId(String floorId);

	Optional<Room> findByIdAndStatusNot(String id, RoomStatus status);
}
