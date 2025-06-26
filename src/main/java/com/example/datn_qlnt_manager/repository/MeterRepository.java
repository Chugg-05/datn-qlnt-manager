package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.common.MeterType;
import com.example.datn_qlnt_manager.entity.Meter;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MeterRepository extends JpaRepository<Meter, String> {

    @Query("""
               SELECT m
               FROM Meter m
               INNER JOIN Room r ON m.roomId = r.id
               WHERE (:meterType IS NULL OR m.meterType = :meterType)
            """)
    Page<Meter> filterMetersPaging(
            @Param("roomId") String roomId,
            @Param("meterType") MeterType meterType,
            Pageable pageable);

    boolean existsById(String id);

}
