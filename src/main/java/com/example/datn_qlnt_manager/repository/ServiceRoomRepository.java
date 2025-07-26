package com.example.datn_qlnt_manager.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.datn_qlnt_manager.dto.response.IdAndName;
import com.example.datn_qlnt_manager.dto.response.IdNamAndType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.common.ServiceRoomStatus;
import com.example.datn_qlnt_manager.dto.statistics.ServiceRoomStatistics;
import com.example.datn_qlnt_manager.entity.ServiceRoom;

@Repository
public interface ServiceRoomRepository extends JpaRepository<ServiceRoom, String> {
    boolean existsByRoomIdAndServiceId(String roomId, String serviceId);

    Optional<ServiceRoom> findByIdAndServiceRoomStatusNot(String id, ServiceRoomStatus status);

    @Query(
            """
                    	SELECT sr FROM ServiceRoom sr
                    	WHERE sr.room.floor.building.user.id = :userId
                    	AND (:query IS NULL OR
                    		LOWER(sr.room.roomCode) LIKE LOWER(CONCAT('%', :query, '%')) OR
                    		LOWER(sr.service.name) LIKE LOWER(CONCAT('%', :query, '%')) OR
                    		LOWER(sr.usageCode) LIKE LOWER(CONCAT('%', :query, '%')) OR
                    		LOWER(sr.descriptionServiceRoom) LIKE LOWER(CONCAT('%', :query, '%'))
                    	)
                    	AND (:minPrice IS NULL OR sr.totalPrice >= :minPrice)
                    	AND (:maxPrice IS NULL OR sr.totalPrice <= :maxPrice)
                    	AND (:status IS NULL OR sr.serviceRoomStatus = :status)
                    	AND sr.serviceRoomStatus != 'DA_HUY'
                    """)
    Page<ServiceRoom> filterServiceRoomsPaging(
            @Param("userId") String userId,
            @Param("query") String query,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("status") ServiceRoomStatus status,
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
                    """)
    ServiceRoomStatistics countByStatus(@Param("userId") String userId);

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

    @Query("""
            	SELECT new com.example.datn_qlnt_manager.dto.response.IdNamAndType(
                        	sr.id,
                            CONCAT(sr.service.name, ' - ' ,CAST(sr.totalPrice as string ), ' VND') ,
                            CAST(sr.service.serviceCategory as string ) 
                ) 
                    FROM ServiceRoom sr
            		WHERE sr.service.user.id = :userId AND sr.room.id = :roomId
            """)
    List<IdNamAndType> getAllServiceRoomByUserId(@Param("userId") String userId, @Param("roomId") String roomId);
}
