package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.common.MeterType;
import com.example.datn_qlnt_manager.dto.response.meter.MeterResponse;
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

    boolean existsByMeterCode(String meterCode);

//    @Query("""
//                SELECT m
//                FROM Meter m
//                JOIN m.room r
//                JOIN r.floor f
//                JOIN f.building b
//                JOIN b.user u
//                WHERE u.id = :userId
//                  AND (:buildingId IS NULL OR b.id = :buildingId)
//                  AND (:roomCode IS NULL OR r.roomCode = :roomCode)
//                  AND (:meterType IS NULL OR m.meterType = :meterType)
//                  AND (
//                    :query IS NULL OR
//                    LOWER(m.meterCode) LIKE LOWER(CONCAT('%', :query, '%')) OR
//                    LOWER(m.meterName) LIKE LOWER(CONCAT('%', :query, '%')) OR
//                    LOWER(m.descriptionMeter) LIKE LOWER(CONCAT('%', :query, '%'))
//                  )
//                ORDER BY m.updatedAt DESC
//            """)
//    Page<Meter> findByUserIdWithFilter(
//            @Param("userId") String userId,
//            @Param("buildingId") String buildingId,
//            @Param("roomCode") String roomCode,
//            @Param("meterType") MeterType meterType,
//            @Param("query") String query,
//            Pageable pageable
//    );

    @Query("""
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
                    m.initialIndex,
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
            Pageable pageable
    );


    @Query("""
                SELECT m FROM Meter m
                JOIN FETCH m.service s
                WHERE m.room.id = :roomId
            """)
    List<Meter> findByRoomId(@Param("roomId") String roomId);


}
