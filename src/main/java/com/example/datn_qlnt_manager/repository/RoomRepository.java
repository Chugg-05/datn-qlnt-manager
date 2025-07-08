package com.example.datn_qlnt_manager.repository;

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
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {

    @Query("""
                SELECT r
                FROM Room r
                INNER JOIN Floor f ON r.floor = f
                WHERE (:buildingId IS NULL OR f.building.id = :buildingId)
                AND (:status IS NULL OR r.status = :status)
                AND (:maxPrice IS NULL OR r.price <= :maxPrice)
                AND (:minPrice IS NULL OR r.price >= :minPrice)
                AND (:maxAcreage IS NULL OR r.acreage <= :maxAcreage)
                AND (:minAcreage IS NULL OR r.acreage >= :minAcreage)
                AND (:maxPerson IS NULL OR r.maximumPeople <= :maxPerson)
                AND (:nameFloor IS NULL OR f.nameFloor LIKE CONCAT('%', :nameFloor, '%'))
                AND r.status != com.example.datn_qlnt_manager.common.RoomStatus.HUY_HOAT_DONG
                AND (f.building.user IS NOT NULL AND f.building.user.id = :userId)
                ORDER BY f.updatedAt DESC
            """)
    Page<Room> getPageAndSearchAndFilterRoomByUserId(
            @Param("userId") String userId,
            @Param("buildingId") String buildingId,
            @Param("status") String status,
            @Param("maxPrice") Double maxPrice,
            @Param("minPrice") Double minPrice,
            @Param("maxAcreage") Double maxAcreage,
            @Param("minAcreage") Double minAcreage,
            @Param("maxPerson") Integer maxPerson,
            @Param("nameFloor") String nameFloor,
            Pageable pageable);

    @Query("""
                SELECT r
                FROM Room r
                INNER JOIN Floor f ON r.floor = f
                WHERE (:buildingId IS NULL OR f.building.id = :buildingId)
                AND (:maxPrice IS NULL OR r.price <= :maxPrice)
                AND (:minPrice IS NULL OR r.price >= :minPrice)
                AND (:maxAcreage IS NULL OR r.acreage <= :maxAcreage)
                AND (:minAcreage IS NULL OR r.acreage >= :minAcreage)
                AND (:maxPerson IS NULL OR r.maximumPeople <= :maxPerson)
                AND (:nameFloor IS NULL OR f.nameFloor LIKE CONCAT('%', :nameFloor, '%'))
                AND r.status = 'HUY_HOAT_DONG'
                AND (f.building.user IS NOT NULL AND f.building.user.id = :userId)
                ORDER BY f.updatedAt DESC
            """)
    Page<Room> getRoomWithStatusCancelByUserId(
            @Param("userId") String userId,
            @Param("buildingId") String buildingId,
            @Param("maxPrice") Double maxPrice,
            @Param("minPrice") Double minPrice,
            @Param("maxAcreage") Double maxAcreage,
            @Param("minAcreage") Double minAcreage,
            @Param("maxPerson") Integer maxPerson,
            @Param("nameFloor") String nameFloor,
            Pageable pageable);

    @Query("""
                SELECT new com.example.datn_qlnt_manager.dto.response.room.RoomCountResponse(
                    :buildingId,
                    COUNT(r.id),
                    SUM(CASE WHEN r.status = 'TRONG' THEN 1 ELSE 0 END),
                    SUM(CASE WHEN r.status = 'DANG_THUE' THEN 1 ELSE 0 END),
                    SUM(CASE WHEN r.status = 'DA_DAT_COC' THEN 1 ELSE 0 END),
                    SUM(CASE WHEN r.status = 'DANG_BAO_TRI' THEN 1 ELSE 0 END),
                    SUM(CASE WHEN r.status = 'CHUA_HOAN_THIEN' THEN 1 ELSE 0 END),
                    SUM(CASE WHEN r.status = 'TAM_KHOA' THEN 1 ELSE 0 END)
                )
                FROM Room r
                WHERE r.floor.building.id = :buildingId
                AND r.status != 'HUY_HOAT_DONG'
            """)
    RoomCountResponse getRoomStatsByBuilding(@Param("buildingId") String buildingId);

    @Query("""
            	SELECT r FROM Room r
            	WHERE r.floor.building.user.id = :userId
            	ORDER BY r.updatedAt DESC
            """)
    List<Room> findAllRoomsByUserId(@Param("userId") String userId);

    @Query("""
                SELECT new com.example.datn_qlnt_manager.dto.response.IdAndName(r.id, r.roomCode)
                FROM Room r
                WHERE r.floor.building.user.id = :userId
                 AND r.status != 'HUY_HOAT_DONG'
                 AND r.floor.id = :floorId
            """)
    List<IdAndName> findRoomsByUserIdAndFloorId(@Param("userId") String userId, @Param("floorId") String floorId);

    @Query("SELECT r.roomCode FROM Room r WHERE r.floor.building.id = :buildingId AND r.floor.id = :floorId")
    List<String> findRoomCodesByBuildingAndFloor(@Param("buildingId") String buildingId,
                                                 @Param("floorId") String floorId);

    int countByFloorId(String floorId);


}