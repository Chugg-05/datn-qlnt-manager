package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.common.MeterType;
import com.example.datn_qlnt_manager.entity.MeterReading;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReading, String> {
    @Query("""
    SELECT mr FROM MeterReading mr
    JOIN mr.meter m
    JOIN m.room r
    JOIN r.floor f
    JOIN f.building b
    JOIN b.user u
    WHERE u.id = :userId
      AND (:buildingId IS NULL OR b.id = :buildingId)
      AND (:roomCode IS NULL OR r.roomCode = :roomCode)
      AND (:meterType IS NULL OR m.meterType = :meterType)
      AND (:month IS NULL OR mr.month = :month)
    ORDER BY mr.updatedAt DESC
""")
    Page<MeterReading> filterMeterReadings(
            @Param("userId") String userId,
            @Param("buildingId") String buildingId,
            @Param("roomCode") String roomCode,
            @Param("meterType") MeterType meterType,
            @Param("month") Integer month,
            Pageable pageable
    );

    @Query("""
        SELECT mr FROM MeterReading mr
        WHERE mr.meter.id = :meterId
        AND mr.month = :month
        AND mr.year = :year
    """)
    Optional<MeterReading> findByMeterIdAndMonthAndYear(
            @Param("meterId") String meterId,
            @Param("month") Integer month,
            @Param("year") Integer year
    );

}
