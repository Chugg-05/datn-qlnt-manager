package com.example.datn_qlnt_manager.service.strategy;

import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.service.RedisService;
import com.example.datn_qlnt_manager.utils.CodeGeneratorUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BuildingCodeStrategy {
    RedisService redisService;

    public String generate(User user) {
        String prefix = CodeGeneratorUtil.generatePrefixFromName(user.getFullName());
        String buildingCode;

        do {
            String randomNum = String.format("%06d", new SecureRandom().nextInt(1_000_000));
            buildingCode = prefix + randomNum;
        } while (redisService.exists("codegen:building:" + buildingCode));

        redisService.markAsUsed("codegen:building:" + buildingCode);

        return buildingCode;
    }
}
