package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.dto.response.vehicle.VehicleBasicResponse;
import com.example.datn_qlnt_manager.entity.ContractVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContractVehicleRepository extends JpaRepository<ContractVehicle, String> {

    @Query("""
        SELECT new com.example.datn_qlnt_manager.dto.response.vehicle.VehicleBasicResponse(
           cv.id,
           c.id,
           t.id,
           v.id,
           t.fullName,
           v.vehicleType,
           v.licensePlate,
           v.describe
        )
        FROM Vehicle v
        JOIN v.tenant t
        JOIN v.contractVehicles cv
        JOIN cv.contract c
        WHERE c.id = :contractId
    """)
    List<VehicleBasicResponse> findAllVehicleBasicResponseByContractId(@Param("contractId") String contractId);

    @Query("""
        SELECT CASE WHEN COUNT(cv) > 0 THEN TRUE ELSE FALSE END
        FROM ContractVehicle cv
        JOIN cv.contract c
        JOIN cv.vehicle v
        WHERE v.id = :vehicleId
          AND c.startDate <= :checkDate
          AND (c.endDate IS NULL OR c.endDate >= :checkDate)
          AND cv.startDate <= :checkDate
          AND (cv.endDate IS NULL OR cv.endDate >= :checkDate)
    """)
    boolean existsActiveContractForVehicle(@Param("vehicleId") String vehicleId,
                                           @Param("checkDate") LocalDate checkDate);



}
