package com.example.datn_qlnt_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.datn_qlnt_manager.entity.Building;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingRepository extends JpaRepository<Building, String> {}
