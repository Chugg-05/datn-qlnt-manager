package com.example.datn_qlnt_manager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
    @Query(
            """
		SELECT p FROM Permission p
		WHERE (:name IS NULL OR p.name LIKE CONCAT('%', :name, '%'))
		""")
    Page<Permission> filterPermissionsPaging(
            @Param("name") String name,
            Pageable pageable);

    boolean existsByName(String name);
}
