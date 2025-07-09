package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.common.MeterType;
import com.example.datn_qlnt_manager.entity.Meter;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeterRepository extends JpaRepository<Meter, String> {

    @Query("""
                SELECT m
                FROM Meter m
                JOIN m.room r
                JOIN r.floor f
                JOIN f.building b
                WHERE (:buildingId IS NULL OR b.id = :buildingId)
                  AND (:roomCode IS NULL OR r.roomCode = :roomCode)
                  AND (:meterType IS NULL OR m.meterType = :meterType)
                ORDER BY m.updatedAt DESC
            """)
    Page<Meter> filterMetersPaging(
            @Param("buildingId") String buildingId,
            @Param("roomCode") String roomCode,
            @Param("meterType") MeterType meterType,
            Pageable pageable
    );


    @Query("""
        SELECT m FROM Meter m
        JOIN FETCH m.service s
        WHERE m.room.id = :roomId
    """)
    List<Meter> findByRoomId(@Param("roomId") String roomId);
}
