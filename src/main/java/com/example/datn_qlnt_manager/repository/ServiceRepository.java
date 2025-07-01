package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.entity.Service;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<Service, String> {

    @Query("""
            SELECT s 
            FROM Service s 
            WHERE (:name IS NULL OR s.name LIKE CONCAT('%', :name, '%'))
            """)
    Page<Service> filterServicePaging(
            @Param("name") String name,
            Pageable pageable
    );

    // boolean existsByTenDichVu(String name);
}
