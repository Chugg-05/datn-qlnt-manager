package com.example.datn_qlnt_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.entity.Floor;

@Repository
public interface FloorRepository extends JpaRepository<Floor, String> {}
