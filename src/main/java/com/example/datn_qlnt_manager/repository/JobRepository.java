package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {

}
