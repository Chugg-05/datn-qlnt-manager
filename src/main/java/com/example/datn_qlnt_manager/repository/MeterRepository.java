package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.common.MeterType;
import com.example.datn_qlnt_manager.entity.Meter;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MeterRepository extends JpaRepository<Meter, String> {

    @Query("""
                SELECT m
                FROM Meter m
                JOIN Room r ON m.roomCode = r.roomCode
                JOIN Floor f ON r.floor = f
                JOIN Building b ON f.building = b
                WHERE (:buildingId IS NULL OR b.id = :buildingId)
                  AND (:roomCode IS NULL OR r.roomCode = :roomCode)
                  AND (:meterType IS NULL OR m.meterType = :meterType)
                  AND (
                      :keyword IS NULL OR
                      LOWER(m.meterName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                      LOWER(m.meterCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  )
            """)
    Page<Meter> filterMetersPaging(
            @Param("buildingId") String buildingId,
            @Param("roomCode") String roomCode,
            @Param("meterType") MeterType meterType,
            @Param("keyword") String keyword,
            Pageable pageable
    );


    boolean existsById(String id);

}
