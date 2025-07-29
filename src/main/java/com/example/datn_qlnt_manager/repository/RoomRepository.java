package com.example.datn_qlnt_manager.repository;

import java.util.List;
import java.util.Optional;

import com.example.datn_qlnt_manager.dto.response.room.RoomDetailsResponse;
import com.example.datn_qlnt_manager.dto.statistics.RoomNoServiceStatisticResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.dto.response.IdAndName;
import com.example.datn_qlnt_manager.dto.response.room.RoomCountResponse;
import com.example.datn_qlnt_manager.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {

    @Query(
            """
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
				AND (:floorId IS NULL OR r.floor.id = :floorId)
				ORDER BY f.updatedAt DESC
			""")
    Page<Room> getPageAndSearchAndFilterRoomByUserId(
            @Param("userId") String userId,
            @Param("buildingId") String buildingId,
            @Param("status") RoomStatus status,
            @Param("maxPrice") Double maxPrice,
            @Param("minPrice") Double minPrice,
            @Param("maxAcreage") Double maxAcreage,
            @Param("minAcreage") Double minAcreage,
            @Param("maxPerson") Integer maxPerson,
            @Param("nameFloor") String nameFloor,
            @Param("floorId") String floorId,
            Pageable pageable);

    @Query(
            """
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

    @Query(
            """
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

    @Query(
            """
				SELECT r FROM Room r
				WHERE r.floor.building.user.id = :userId
				ORDER BY r.updatedAt DESC
			""")
    List<Room> findAllRoomsByUserId(@Param("userId") String userId);

    @Query(
            """
				SELECT new com.example.datn_qlnt_manager.dto.response.IdAndName(r.id, r.roomCode)
				FROM Room r
				WHERE r.floor.building.user.id = :userId
				AND r.status != 'HUY_HOAT_DONG'
				AND r.floor.id = :floorId
			""")
    List<IdAndName> findRoomsByUserIdAndFloorId(@Param("userId") String userId, @Param("floorId") String floorId);

    @Query("SELECT r.roomCode FROM Room r WHERE r.floor.building.id = :buildingId AND r.floor.id = :floorId")
    List<String> findRoomCodesByBuildingAndFloor(
            @Param("buildingId") String buildingId, @Param("floorId") String floorId);

    int countByFloorId(String floorId);

    @Query(
            """
				SELECT new com.example.datn_qlnt_manager.dto.response.IdAndName(
					r.id,
					CONCAT(
						COALESCE(r.roomCode, ''),
						' - ',
						COALESCE(f.nameFloor, ''),
						' - ',
						COALESCE(b.buildingName, '')
					)
				)
				FROM Room r
				LEFT JOIN r.floor f
				LEFT JOIN f.building b
				WHERE r.status != 'HUY_HOAT_DONG' AND b.user.id = :userId
			""")
    List<IdAndName> getServiceRoomInfoByUserId(@Param("userId") String userId);

    @Query(
            """
                    	SELECT new com.example.datn_qlnt_manager.dto.response.IdAndName(
                    		r.id,
                    		CONCAT(r.roomCode, ' - ', f.nameFloor, ' - ', b.buildingName)
                    	)
                    	FROM Room r
                    	LEFT JOIN r.floor f
                    	LEFT JOIN f.building b
                    	WHERE r.status != 'HUY_HOAT_DONG' AND b.user.id = :userId
                    				AND b.id =:buildingId
                    """)
    List<IdAndName> getRoomInfoByUserId(@Param("userId") String userId, @Param("buildingId") String buildingId);

	@Query("""
    SELECT DISTINCT c.room FROM Contract c
    JOIN c.tenants t
    WHERE t.id = :tenantId
""")
	List<Room> findRoomsByTenantId(@Param("tenantId") String tenantId);

	List<Room> findByFloorBuildingId(String buildingId);

	@Query("""
    SELECT r FROM Room r
    JOIN r.floor f
    JOIN f.building b
    WHERE
        r.floor.building.user.id = :userId
        AND EXISTS (
            SELECT c FROM Contract c
            WHERE c.room = r AND c.status = 'HIEU_LUC'
        )
        AND NOT EXISTS (
            SELECT sr FROM ServiceRoom sr
            WHERE sr.room = r
        )
        AND (:buildingId IS NULL OR b.id = :buildingId)
""")
	Page<Room> findActiveRoomsWithoutServiceRoomByUser(
			@Param("userId") String userId,
			@Param("buildingId") String buildingId,
			Pageable pageable);

	@Query("""
        SELECT new com.example.datn_qlnt_manager.dto.statistics.RoomNoServiceStatisticResponse(
            b.id, COUNT(r)
        )
        FROM Room r
        JOIN r.floor f
        JOIN f.building b
        WHERE r.floor.building.user.id = :userId
          AND EXISTS (
              SELECT c FROM Contract c
              WHERE c.room = r AND c.status = 'HIEU_LUC'
          )
          AND NOT EXISTS (
              SELECT sr FROM ServiceRoom sr
              WHERE sr.room = r
          )
          AND (:buildingId IS NULL OR b.id = :buildingId)
        GROUP BY b.id
    """)
	List<RoomNoServiceStatisticResponse> countRoomsWithoutServiceByUser(
			@Param("userId") String userId,
			@Param("buildingId") String buildingId
	);

	@Query("""
    SELECT DISTINCT r
    FROM Room r
    LEFT JOIN FETCH r.assets
    WHERE r.floor.building.user.id = :userId
""")
	List<Room> findAllWithAssets(@Param("userId") String userId);

	@Query("""
    SELECT r
    FROM Room r
    JOIN r.floor.building b
    WHERE b.id = :buildingId
""")
	List<Room> findAllByBuilding(@Param("buildingId") String buildingId);

	@Query("""
    	SELECT COUNT(r)
    	FROM Room r
    	JOIN r.floor f
    	JOIN f.building b
    	WHERE b.user.id = :userId
    	AND r.status = 'TRONG'
     	AND r.id NOT IN (
        	SELECT c.room.id FROM Contract c
    )
""")
	long StatisticRoomsWithoutContract(@Param("userId") String userId);

	@Query("""
<<<<<<< Updated upstream
		SELECT r FROM Room r
		JOIN r.floor f
		JOIN f.building b
		WHERE b.user.id = :userId
		AND (b.id = :buildingId)
		AND (r.status != 'HUY_HOAT_DONG')
		AND r.id NOT IN (
			SELECT ar.room.id FROM AssetRoom ar
    	)
""")
	Page<Room> findRoomsWithoutAssetsByUserId(
			@Param("userId") String userId,
			@Param("buildingId") String buildingId,
			Pageable pageable
	);

	@Query("""
		SELECT COUNT(r) FROM Room r
		JOIN r.floor f
		JOIN f.building b
		WHERE b.user.id = :userId
		AND (b.id = :buildingId)
		AND (r.status != 'HUY_HOAT_DONG')
		AND r.id NOT IN (
			SELECT ar.room.id FROM AssetRoom ar
			)
""")
	long StatisticRoomWithoutAssets(
			@Param("userId") String userId,
			@Param("buildingId") String buildingId
			);
=======
    SELECT new com.example.datn_qlnt_manager.dto.response.room.RoomDetailsResponse(
        b.buildingName, b.address, owner.fullName, owner.phoneNumber,
        r.roomCode, r.acreage, r.maximumPeople, r.roomType, r.status, r.description,
        ctr.contractCode, ctr.numberOfPeople,
        rep.fullName, rep.phoneNumber,rep.dob,rep.identityCardNumber,
        ctr.deposit, ctr.roomPrice, ctr.status, ctr.startDate, ctr.endDate,
        CAST((SELECT COUNT(DISTINCT t.id) FROM Contract c1 JOIN c1.tenants t WHERE c1 = ctr) AS long),
        CAST((SELECT COUNT(ar.id) FROM AssetRoom ar WHERE ar.room = r) AS long),
        CAST((SELECT COUNT(DISTINCT s.id) FROM Contract c2 JOIN c2.services s WHERE c2 = ctr) AS long),
        CAST((SELECT COUNT(DISTINCT v.id) FROM Contract c3 JOIN c3.vehicles v WHERE c3 = ctr) AS long)
    )
    FROM Room r
    JOIN r.floor f
    JOIN f.building b
    JOIN b.user owner
    JOIN Contract ctr ON ctr.room = r AND ctr.status = com.example.datn_qlnt_manager.common.ContractStatus.HIEU_LUC
    JOIN ctr.tenants tenant
    JOIN ctr.tenants rep ON rep.isRepresentative = true
    WHERE r.id = :roomId AND tenant.id = :userId
""")
	Optional<RoomDetailsResponse> findRoomDetailsForTenant(@Param("roomId") String roomId, @Param("userId") String userId);
>>>>>>> Stashed changes
}
