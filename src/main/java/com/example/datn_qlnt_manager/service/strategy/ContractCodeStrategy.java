package com.example.datn_qlnt_manager.service.strategy;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.entity.Building;
import com.example.datn_qlnt_manager.entity.Room;
import com.example.datn_qlnt_manager.service.RedisService;
import com.example.datn_qlnt_manager.utils.CodeGeneratorUtil;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContractCodeStrategy {
    RedisService redisService;

    public String generate(Room room) {
        Building building = room.getFloor().getBuilding();
        String buildingPrefix = CodeGeneratorUtil.generatePrefixFromName(building.getBuildingName());
        String roomCode = room.getRoomCode();

        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMM"));
        String redisKey = String.format("codegen:contract:%s:%s:%s", building.getId(), room.getId(), datePart);

        long sequence = redisService.increment(redisKey);
        String sequencePart = String.format("%04d", sequence);

        return buildingPrefix + roomCode + datePart + sequencePart;
    }
}
