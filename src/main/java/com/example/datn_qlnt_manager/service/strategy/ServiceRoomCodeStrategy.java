package com.example.datn_qlnt_manager.service.strategy;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.entity.Room;
import com.example.datn_qlnt_manager.service.RedisService;
import com.example.datn_qlnt_manager.utils.CodeGeneratorUtil;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServiceRoomCodeStrategy {

    RedisService redisService;

    public String generate(Room room) {
        String prefix = CodeGeneratorUtil.generatePrefixFromName(room.getRoomCode());
        String usageCode;

        do {
            String randomNum = String.format("%06d", new SecureRandom().nextInt(1_000_000));
            usageCode = "MSD-" + prefix + "-" + randomNum;
        } while (redisService.exists("codegen:ServiceRoom:" + usageCode));
        redisService.markAsUsed("codegen:ServiceRoom:" + usageCode);
        return usageCode;
    }
}
