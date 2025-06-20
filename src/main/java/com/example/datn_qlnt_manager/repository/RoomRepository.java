package com.example.datn_qlnt_manager.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.entity.Room;

import io.lettuce.core.dynamic.annotation.Param;

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

	boolean existsByRoomId(String roomId);
}
