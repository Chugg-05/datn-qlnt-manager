package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.common.VehicleType;
import com.example.datn_qlnt_manager.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    @Query("""
        SELECT v FROM Vehicle v
        INNER JOIN v.tenant t
        WHERE (:vehicleType IS NULL OR v.vehicleType = :vehicleType)
        AND (:licensePlate IS NULL OR v.licensePlate LIKE CONCAT('%', :licensePlate, '%') )
        AND (:userId IS NULL OR t.user.id = :userId)
        AND (:tenantId IS NULL OR t.id = :tenantId)
        """)
    Page<Vehicle> filterVehiclePaging (
            @Param("userId") String userId,
            @Param("tenantId") String tenantId,
            @Param("vehicleType") VehicleType vehicleType,
            @Param("licensePlate") String licensePlate,
            Pageable pageable
            );


    boolean existsByLicensePlate(String licensePlate);//check trùng biển số xe

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.tenant.user.id = :userId")
    long countAll(@Param("userId") String userId);

    @Query("SELECT v.vehicleType, COUNT(v) FROM Vehicle v WHERE v.tenant.user.id = :userId GROUP BY v.vehicleType")
    List<Object[]> countByVehicleType(@Param("userId") String userId);
}
