package com.example.datn_qlnt_manager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.common.MeterType;
import com.example.datn_qlnt_manager.dto.response.meter.MeterReadingMonthlyStatsResponse;
import com.example.datn_qlnt_manager.entity.MeterReading;

import feign.Param;

@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReading, String> {
    @Query(
            """
	SELECT mr FROM MeterReading mr
	JOIN mr.meter m
	JOIN m.room r
	JOIN r.floor f
	JOIN f.building b
	JOIN b.user u
	WHERE u.id = :userId
	AND (:buildingId IS NULL OR b.id = :buildingId)
	AND (:roomId IS NULL OR r.id = :roomId)
	AND (:meterType IS NULL OR m.meterType = :meterType)
	AND (:month IS NULL OR mr.month = :month)
	ORDER BY mr.updatedAt DESC
""")
    Page<MeterReading> filterMeterReadings(
            @Param("userId") String userId,
            @Param("buildingId") String buildingId,
            @Param("roomId") String roomId,
            @Param("meterType") MeterType meterType,
            @Param("month") Integer month,
            Pageable pageable);

    @Query(
            """
		SELECT mr FROM MeterReading mr
		WHERE mr.meter.id = :meterId
		AND mr.month = :month
		AND mr.year = :year
	""")
    Optional<MeterReading> findByMeterIdAndMonthAndYear(
            @Param("meterId") String meterId, @Param("month") Integer month, @Param("year") Integer year);

    @Query(
            """
				SELECT new com.example.datn_qlnt_manager.dto.response.meter.MeterReadingMonthlyStatsResponse(
					m.meterCode,
					mr.month,
					mr.year,
					mr.oldIndex,
					mr.newIndex,
					mr.quantity,
					m.meterType
				)
				FROM MeterReading mr
				JOIN mr.meter m
				WHERE m.room.id = :roomId OR m.service.user.id = :userId
				ORDER BY mr.year DESC, mr.month DESC
			""")
    List<MeterReadingMonthlyStatsResponse> getMonthlyStats(
            @Param("roomId") String roomId, @Param("userId") String userId);

    boolean existsByMeterIdAndMonthAndYear(String meterId, int month, int year);

	List<MeterReading> findAllByMeterCode(String meterCode);
}
