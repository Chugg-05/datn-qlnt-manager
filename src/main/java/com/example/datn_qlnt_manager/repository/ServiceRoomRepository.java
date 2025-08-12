package com.example.datn_qlnt_manager.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.common.RoomType;
import com.example.datn_qlnt_manager.common.ServiceRoomStatus;
import com.example.datn_qlnt_manager.dto.projection.ServiceRoomView;
import com.example.datn_qlnt_manager.dto.response.IdNameAndType;
import com.example.datn_qlnt_manager.dto.statistics.ServiceRoomStatistics;
import com.example.datn_qlnt_manager.entity.Room;
import com.example.datn_qlnt_manager.entity.Service;
import com.example.datn_qlnt_manager.entity.ServiceRoom;

@Repository
public interface ServiceRoomRepository extends JpaRepository<ServiceRoom, String> {

    Optional<ServiceRoom> findByIdAndServiceRoomStatusNot(String id, ServiceRoomStatus status);

    @Query(
            """
				SELECT new com.example.datn_qlnt_manager.dto.projection.ServiceRoomView(
					r.id,
					r.roomCode,
					SIZE(r.serviceRooms),
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
    Page<ServiceRoomView> getServiceRoomsPaging(
            @Param("userId") String userId,
            @Param("query") String query,
            @Param("building") String building,
            @Param("floor") String floor,
            @Param("roomType") RoomType roomType,
            @Param("status") RoomStatus status,
            Pageable pageable);

    // thống kê theo trạng thái
    @Query(
            """
						SELECT
							COUNT(sr),
							SUM(CASE WHEN sr.serviceRoomStatus = 'DANG_SU_DUNG' THEN 1 ELSE 0 END),
							SUM(CASE WHEN sr.serviceRoomStatus = 'TAM_DUNG' THEN 1 ELSE 0 END)
						FROM ServiceRoom sr
						WHERE sr.room.floor.building.user.id = :userId
						AND sr.room.floor.building.id = :buildingId
					""")
    ServiceRoomStatistics countByStatus(@Param("userId") String userId, @Param("buildingId") String buildingId);

    @Query(
            """
						SELECT sr FROM ServiceRoom sr
						JOIN FETCH sr.service s
						WHERE sr.room.id = :roomId
						AND sr.startDate <= :startOfMonth
						AND sr.serviceRoomStatus = 'DANG_SU_DUNG'
					""")
    List<ServiceRoom> findActiveByRoomIdAndMonth(
            @Param("roomId") String roomId, @Param("startOfMonth") LocalDate startOfMonth);

    @Query(
            """
				SELECT new com.example.datn_qlnt_manager.dto.response.IdNameAndType(
							sr.id,
							CONCAT(sr.service.name, ' - ' ,CAST(sr.unitPrice as string ), ' VND') ,
							CAST(sr.service.serviceCategory as string )
				)
					FROM ServiceRoom sr
					WHERE sr.service.user.id = :userId AND sr.room.id = :roomId
			""")
    List<IdNameAndType> getAllServiceRoomByUserId(@Param("userId") String userId, @Param("roomId") String roomId);

    @Query("""
		SELECT sr FROM ServiceRoom sr
			JOIN FETCH sr.service
			WHERE sr.room = :room
		""")
    List<ServiceRoom> findAllByRoomWithService(@Param("room") Room room);

    @Query("""
		SELECT sr FROM ServiceRoom sr
		JOIN FETCH sr.room
		WHERE sr.service.id = :serviceId
	""")
    List<ServiceRoom> findAllByServiceWithRoom(@Param("serviceId") String serviceId);

    @Query(
            """
		SELECT sr FROM ServiceRoom sr
		WHERE sr.service.id = :serviceId
		AND sr.room.floor.building.id = :buildingId
	""")
    List<ServiceRoom> findAllByServiceAndBuilding(
            @Param("serviceId") String serviceId, @Param("buildingId") String buildingId);

    boolean existsByRoomAndService(Room room, Service service);

    List<ServiceRoom> findByRoomId(String roomId);

    List<ServiceRoom> findByRoomIdAndServiceRoomStatus(String roomId, ServiceRoomStatus serviceRoomStatus);
}
