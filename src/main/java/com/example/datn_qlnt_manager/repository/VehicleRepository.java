package com.example.datn_qlnt_manager.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.common.VehicleStatus;
import com.example.datn_qlnt_manager.common.VehicleType;
import com.example.datn_qlnt_manager.entity.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    @Query(
            """
					SELECT v FROM Vehicle v
					INNER JOIN v.tenant t
					WHERE (:vehicleType IS NULL OR v.vehicleType = :vehicleType)
					AND (:licensePlate IS NULL OR v.licensePlate LIKE CONCAT('%', :licensePlate, '%') )
					AND (:userId IS NULL OR t.owner.id = :userId)
					AND v.vehicleStatus != 'KHONG_SU_DUNG'
					ORDER BY v.updatedAt DESC
					""")
    Page<Vehicle> getPageAndSearchAndFilterVehicleByUserId(
            @Param("userId") String userId,
            @Param("vehicleType") VehicleType vehicleType,
            @Param("licensePlate") String licensePlate,
            Pageable pageable);

    @Query(
            """
		SELECT v FROM Vehicle v
		INNER JOIN v.tenant t
		WHERE (:vehicleType IS NULL OR v.vehicleType = :vehicleType)
		AND (:licensePlate IS NULL OR v.licensePlate LIKE CONCAT('%', :licensePlate, '%') )
		AND (:userId IS NULL OR t.owner.id = :userId)
		AND v.vehicleStatus = 'KHONG_SU_DUNG'
		ORDER BY v.updatedAt DESC
		""")
    Page<Vehicle> getVehicleWithStatusCancelByUserId(
            @Param("userId") String userId,
            @Param("vehicleType") VehicleType vehicleType,
            @Param("licensePlate") String licensePlate,
            Pageable pageable);

    boolean existsByLicensePlate(String licensePlate); // check trùng biển số xe

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.tenant.owner.id = :userId")
    long countAll(@Param("userId") String userId);

    @Query("SELECT v.vehicleType, COUNT(v) FROM Vehicle v WHERE v.tenant.owner.id = :userId GROUP BY v.vehicleType")
    List<Object[]> countByVehicleType(@Param("userId") String userId);

    Optional<Vehicle> findByIdAndVehicleStatusNot(String id, VehicleStatus vehicleStatus);

	@Query("""
		SELECT v FROM Vehicle v
		JOIN v.tenant t
		JOIN t.contractTenants ct
		WHERE ct.contract.room.id = :roomId
		AND (ct.contract.status = "HIEU_LUC"
		OR ct.contract.status = "SAP_HET_HAN")
""")
	List<Vehicle> findActiveVehiclesByRoomId (@Param("roomId") String roomId);

	@Query("""
		SELECT DISTINCT v
		FROM Contract c
		JOIN c.contractVehicles cv
		JOIN cv.vehicle v
		WHERE v.id IN :vehicleIds
		  AND c.endDate > :startDate
	""")
	List<Vehicle> findActiveVehiclesInContracts(@Param("vehicleIds") Collection<String> vehicleIds,
											   @Param("startDate") LocalDate startDate);

	@Query("""
		SELECT v
		FROM Contract c
		JOIN c.contractVehicles cv
		JOIN cv.vehicle v
		WHERE v.id IN :vehicleIds
		  AND c.id <> :currentContractId
		  AND c.endDate > :startDate
	""")
	List<Vehicle> findActiveVehiclesInOtherContracts(@Param("vehicleIds") Collection<String> vehicleIds,
													 @Param("currentContractId") String currentContractId,
													 @Param("startDate") LocalDate startDate);

	List<Vehicle> findAllByVehicleStatusAndDeleteAtBefore(VehicleStatus vehicleStatus, LocalDate deleteAtBefore);
}
