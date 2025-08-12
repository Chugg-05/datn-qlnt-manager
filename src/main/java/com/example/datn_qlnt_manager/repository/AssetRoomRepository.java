package com.example.datn_qlnt_manager.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.common.RoomType;
import com.example.datn_qlnt_manager.dto.projection.AssetRoomView;
import com.example.datn_qlnt_manager.dto.statistics.AssetStatusStatistic;
import com.example.datn_qlnt_manager.entity.Asset;
import com.example.datn_qlnt_manager.entity.AssetRoom;
import com.example.datn_qlnt_manager.entity.Room;

@Repository
public interface AssetRoomRepository extends JpaRepository<AssetRoom, String> {
    @Query(
            """
				SELECT new com.example.datn_qlnt_manager.dto.projection.AssetRoomView(
					r.id,
					r.roomCode,
					(
						SELECT COUNT(DISTINCT ar.asset.id)
						FROM AssetRoom ar
						WHERE ar.room.id = r.id
					),
					r.roomType,
					r.status,
					r.description )
				FROM Room r
				WHERE (r.floor.building.user.id = :userId)
				AND ((:query IS NULL OR r.roomCode LIKE CONCAT('%', :query, '%') )
				OR (:query IS NULL OR  r.floor.building.buildingName LIKE  CONCAT('%', :query, '%') )
				OR (:query IS NULL OR  r.floor.nameFloor LIKE  CONCAT('%', :query, '%') ) )
				AND (:building IS NULL OR r.floor.building.id = :building)
				AND (:floor IS NULL OR r.floor.id = :floor)
				AND (:roomType IS NULL OR r.roomType = :roomType)
				AND (:status IS NULL OR r.status = :status)
				AND r.status != 'HUY_HOAT_DONG'
				ORDER BY r.updatedAt DESC
			""")
    Page<AssetRoomView> getAssetRoomsPaging(
            @Param("userId") String userId,
            @Param("query") String query,
            @Param("building") String building,
            @Param("floor") String floor,
            @Param("roomType") RoomType roomType,
            @Param("status") RoomStatus status,
            Pageable pageable);

    @Query("""
		SELECT ar FROM AssetRoom ar
		LEFT JOIN ar.asset a
		WHERE ar.room = :room
	""")
    List<AssetRoom> findAllByRoomWithAsset(@Param("room") Room room);

    @Query(
            """
		SELECT new com.example.datn_qlnt_manager.dto.statistics.AssetStatusStatistic(
			COUNT(ar),
			SUM(CASE WHEN ar.assetStatus = 'HOAT_DONG' THEN 1 ELSE 0 END),
			SUM(CASE WHEN ar.assetStatus = 'HU_HONG' THEN 1 ELSE 0 END),
			SUM(CASE WHEN ar.assetStatus = 'CAN_BAO_TRI' THEN 1 ELSE 0 END),
			SUM(CASE WHEN ar.assetStatus = 'THAT_LAC' THEN 1 ELSE 0 END),
			SUM(CASE WHEN ar.assetStatus = 'KHONG_SU_DUNG' THEN 1 ELSE 0 END)
		)
		FROM AssetRoom ar
		WHERE ar.room.floor.building.id = :buildingId
	""")
    AssetStatusStatistic getAssetStatisticsByBuildingId(@Param("buildingId") String buildingId);

    @Query("SELECT SUM(ar.quantity) FROM AssetRoom ar WHERE ar.asset.id = :assetId")
    Integer sumQuantityByAssetId(@Param("assetId") String assetId);

    boolean existsByRoomAndAsset(Room room, Asset asset);
}
