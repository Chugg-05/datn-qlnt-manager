package com.example.datn_qlnt_manager.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.common.JobObjectType;
import com.example.datn_qlnt_manager.common.JobPriorityLevel;
import com.example.datn_qlnt_manager.common.JobStatus;
import com.example.datn_qlnt_manager.entity.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {
    @Query(
            """
					SELECT j FROM Job j
					WHERE (j.building.user.id = :userId)
					AND (:buildingId IS NULL OR j.building.id = :buildingId)
					AND ((:query IS NULL OR j.jobCode LIKE CONCAT('%', :query, '%') )
					OR (:query IS NULL OR j.title LIKE CONCAT('%', :query, '%') ))
					AND (:jobPriorityLevel IS NULL OR j.jobPriorityLevel = :jobPriorityLevel)
					AND (:jobStatus IS NULL OR j.jobStatus = :jobStatus)
					AND (:jobObjectType IS NULL OR j.jobObjectType = :jobObjectType)
					AND j.jobStatus != 'DA_HUY'
					ORDER BY j.updatedAt DESC
			""")
    Page<Job> getPageAndSearchAndFilterJobByUserId(
            @Param("userId") String userId,
            @Param("buildingId") String buildingId,
            @Param("query") String query,
            @Param("jobPriorityLevel") JobPriorityLevel jobPriorityLevel,
            @Param("jobStatus") JobStatus jobStatus,
            @Param("jobObjectType") JobObjectType jobObjectType,
            Pageable pageable);

    @Query("""
		SELECT j FROM Job j
		WHERE j.building.user.id = :userId
		ORDER BY j.updatedAt DESC
		""")
    List<Job> findAllByUserId(@Param("userId") String userId);
}
