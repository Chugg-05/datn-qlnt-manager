package com.example.datn_qlnt_manager.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    @Query(
            """
		SELECT r FROM Role r
		WHERE (:name IS NULL OR r.name LIKE CONCAT('%', :name, '%'))
		""")
    Page<Role> filterRolesPaging(
            @Param("name") String name,
            Pageable pageable);

    boolean existsByName(String name);

    Optional<Role> findByName(String name);
}
