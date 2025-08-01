package com.example.datn_qlnt_manager.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.example.datn_qlnt_manager.entity.ServiceRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.common.MeterType;
import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.common.RoomType;
import com.example.datn_qlnt_manager.dto.response.IdAndName;
import com.example.datn_qlnt_manager.dto.response.meter.MeterResponse;
import com.example.datn_qlnt_manager.dto.response.meter.RoomNoMeterResponse;
import com.example.datn_qlnt_manager.entity.Meter;

import feign.Param;

@Repository
public interface MeterRepository extends JpaRepository<Meter, String> {

    boolean existsByMeterCode(String meterCode);

	boolean existsByRoomIdAndMeterName(String roomId, String meterName);

	boolean existsByRoomIdAndMeterType(String roomId, MeterType meterType);


    @Query(
            """
				SELECT new com.example.datn_qlnt_manager.dto.response.meter.MeterResponse(
					m.id,
					r.id,
					CONCAT(r.roomCode, ' - ', f.nameFloor, ' - ', b.buildingName) ,
					s.id,
					s.name,
					m.meterType,
					m.meterName,
					m.meterCode,
					m.manufactureDate,
					m.closestIndex,
					m.descriptionMeter,
					m.createdAt,
					m.updatedAt
				)
				FROM Meter m
				JOIN m.room r
				JOIN r.floor f
				JOIN f.building b
				JOIN b.user u
				LEFT JOIN m.service s
				WHERE u.id = :userId
				AND (:buildingId IS NULL OR b.id = :buildingId)
				AND (:roomId IS NULL OR r.id = :roomId)
				AND (:meterType IS NULL OR m.meterType = :meterType)
				AND (
					:query IS NULL OR
					LOWER(m.meterCode) LIKE LOWER(CONCAT('%', :query, '%')) OR
					LOWER(m.meterName) LIKE LOWER(CONCAT('%', :query, '%')) OR
					LOWER(m.descriptionMeter) LIKE LOWER(CONCAT('%', :query, '%'))
				)
				ORDER BY m.updatedAt DESC
			""")
    Page<MeterResponse> findByUserIdWithFilter(
            @Param("userId") String userId,
            @Param("buildingId") String buildingId,
            @Param("roomId") String roomId,
            @Param("meterType") MeterType meterType,
            @Param("query") String query,
            Pageable pageable);

    @Query("""
				SELECT m FROM Meter m
				JOIN FETCH m.service s
				WHERE m.room.id = :roomId
			""")
    List<Meter> findByRoomId(@Param("roomId") String roomId);

    @Query(
            """
			SELECT new com.example.datn_qlnt_manager.dto.response.IdAndName(
				m.id,
				CONCAT(m.meterCode, ' - ', m.meterName)
			)
			FROM Meter m
			JOIN m.room r
			JOIN r.floor f
			JOIN f.building b
			JOIN b.user u
			WHERE u.id = :userId
			ORDER BY m.updatedAt DESC
			""")
    List<IdAndName> findAllByUserId(@Param("userId") String userId);

    @Query(
            """
	SELECT new com.example.datn_qlnt_manager.dto.response.meter.RoomNoMeterResponse(
		r.id, r.floor.nameFloor, r.roomCode, r.price, r.roomType, r.status, r.description
	)
	FROM Room r
	WHERE r.floor.building.user.id = :userId
	AND r.id NOT IN (
		SELECT ct.room.id
		FROM Meter ct
		WHERE ct.room IS NOT NULL
	)
	AND (:query IS NULL OR (
		LOWER(r.floor.nameFloor) LIKE LOWER(CONCAT('%', :query, '%'))
		OR LOWER(r.roomCode) LIKE LOWER(CONCAT('%', :query, '%'))
		OR LOWER(r.description) LIKE LOWER(CONCAT('%', :query, '%'))
	))
	AND (:status IS NULL OR r.status = :status)
	AND (:roomType IS NULL OR r.roomType = :roomType)
	AND (:minPrice IS NULL OR r.price >= :minPrice)
	AND (:maxPrice IS NULL OR r.price <= :maxPrice)
""")
    Page<RoomNoMeterResponse> findRoomsWithoutMeterByUserIdWithFilter(
            @Param("userId") String userId,
            @Param("query") String query,
            @Param("status") RoomStatus status,
            @Param("roomType") RoomType roomType,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    @Query(
            """
			SELECT COUNT(r) FROM Room r
			WHERE r.floor.building.user.id = :userId
			AND r.id NOT IN (
				SELECT DISTINCT ct.room.id FROM Meter ct
				WHERE ct.room IS NOT NULL
			)
		""")
    Long countRoomsWithoutMeterByUserId(@org.springframework.data.repository.query.Param("userId") String userId);

	@Query("SELECT m FROM Meter m WHERE m.service = :serviceRoom")
	Optional<Meter> findByServiceRoom(@Param("serviceRoom") ServiceRoom serviceRoom);
}
