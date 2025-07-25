package com.example.datn_qlnt_manager.service.strategy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.entity.Building;
import com.example.datn_qlnt_manager.entity.Floor;
import com.example.datn_qlnt_manager.repository.RoomRepository;
import com.example.datn_qlnt_manager.service.RedisService;
import com.example.datn_qlnt_manager.utils.CodeGeneratorUtil;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomCodeStrategy {
    RedisService redisService;
    RoomRepository roomRepository;

    public String generate(Building building, Floor floor) {
        String prefix = CodeGeneratorUtil.getFirstCharPrefix(building.getBuildingName());
        Integer floorNumber = CodeGeneratorUtil.extractFloorNumber(floor.getNameFloor());

        if (floorNumber == null || floorNumber < 1) {
            throw new IllegalArgumentException("Tên tầng không hợp lệ: " + floor.getNameFloor());
        }

        List<String> roomCodes = roomRepository.findRoomCodesByBuildingAndFloor(building.getId(), floor.getId());

        Set<Integer> usedNumbers = roomCodes.stream()
                .map(code -> Integer.parseInt(code.replace(prefix, "")))
                .collect(Collectors.toSet());

        for (int i = 1; i <= usedNumbers.size() + 1; i++) {
            int roomNumber = floorNumber * 100 + i;
            if (!usedNumbers.contains(roomNumber)) {
                return prefix + roomNumber;
            }
        }

        String redisKey = "codegen:room:" + building.getId() + ":floor:" + floorNumber;
        long index = redisService.increment(redisKey);
        int roomNumber = floorNumber * 100 + (int) index;

        return prefix + roomNumber;
    }
}
