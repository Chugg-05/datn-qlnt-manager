package com.example.datn_qlnt_manager.service.strategy;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.entity.Building;
import com.example.datn_qlnt_manager.repository.FloorRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FloorNameStrategy {
    FloorRepository floorRepository;

    public String generate(Building building) {
        List<String> existingNames = floorRepository.findAllNamesByBuildingId(building.getId());

        int index = 1;
        while (existingNames.contains("Tầng " + index)) {
            index++;
        }

        return "Tầng " + index;
    }
}
