package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.dto.response.IdAndName;
import com.example.datn_qlnt_manager.dto.response.room.RoomCountResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.entity.Room;


import java.util.List;

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
                    AND r.status!= com.example.datn_qlnt_manager.common.RoomStatus.HUY_HOAT_DONG
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


//	@Query("""
//			SELECT
//				COUNT(CASE WHEN r.status IN (
//					com.example.datn_qlnt_manager.common.RoomStatus.DANG_THUE,
//					com.example.datn_qlnt_manager.common.RoomStatus.DA_DAT_COC
//				) THEN 1 END),
//				SUM(CASE WHEN r.status = 'DANG_THUE' THEN 1 ELSE 0 END),
//				SUM(CASE WHEN r.status = 'DA_DAT_COC' THEN 1 ELSE 0 END)
//			FROM Room r
//			WHERE r.floor.id = :floorId
//		""")
    @Query("""
            	select new com.example.datn_qlnt_manager.dto.response.room.RoomCountResponse(
            	:floorId,
            	count (r.id) ,
            	SUM (case when r.status = com.example.datn_qlnt_manager.common.RoomStatus.TRONG then 1 else 0 end),
            	SUM (case when r.status = com.example.datn_qlnt_manager.common.RoomStatus.DANG_THUE then 1 else 0 end),
            	SUM (case when r.status = com.example.datn_qlnt_manager.common.RoomStatus.DA_DAT_COC then 1 else 0 end),
            	SUM (case when r.status = com.example.datn_qlnt_manager.common.RoomStatus.DANG_BAO_TRI then 1 else 0 end),
            	SUM (case when r.status = com.example.datn_qlnt_manager.common.RoomStatus.CHUA_HOAN_THIEN then 1 else 0 end),
            	SUM (case when r.status = com.example.datn_qlnt_manager.common.RoomStatus.TAM_KHOA then 1 else 0 end)
            	) from Room r 
            	WHERE r.status!= com.example.datn_qlnt_manager.common.RoomStatus.HUY_HOAT_DONG
            """)
    RoomCountResponse getRoomStatsByFloor(@Param("floorId") String floorId);

    @Query("""
                SELECT
                    COUNT(CASE WHEN r.status IN (
                        com.example.datn_qlnt_manager.common.RoomStatus.DANG_THUE,
                        com.example.datn_qlnt_manager.common.RoomStatus.DA_DAT_COC
                    ) THEN 1 END) AS totalInUse,
                    SUM(CASE WHEN r.status = 'DANG_THUE' THEN 1 ELSE 0 END) AS totalDangThue,
                    SUM(CASE WHEN r.status = 'DA_DAT_COC' THEN 1 ELSE 0 END) AS totalDatCoc
                FROM Room r
                WHERE r.floor.building.user.id = :userId
            """)
    RoomCountResponse getRoomStatsByUser(@Param("userId") String userId);

    @Query("""
            	SELECT r FROM Room r
            	WHERE r.floor.building.user.id = :userId
            	ORDER BY r.updatedAt DESC
            """)
    List<Room> findAllRoomsByUserId(@Param("userId") String userId);

    @Query("""
                SELECT new com.example.datn_qlnt_manager.dto.response.IdAndName(r.id, r.roomCode)
                FROM Room r
                JOIN r.floor f
                JOIN f.building b
                WHERE b.user.id = :userId AND r.status != com.example.datn_qlnt_manager.common.RoomStatus.HUY_HOAT_DONG
            """)
    List<IdAndName> findRoomsByUserId(@Param("userId") String userId);

    @Query("SELECT r.roomCode FROM Room r WHERE r.floor.building.id = :buildingId AND r.floor.id = :floorId")
    List<String> findRoomCodesByBuildingAndFloor(@Param("buildingId") String buildingId,
                                                 @Param("floorId") String floorId);

    int countByFloorId(String floorId);
}