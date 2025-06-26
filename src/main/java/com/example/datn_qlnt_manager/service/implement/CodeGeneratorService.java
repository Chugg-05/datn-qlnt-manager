package com.example.datn_qlnt_manager.service.implement;

import java.security.SecureRandom;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.entity.Building;
import com.example.datn_qlnt_manager.entity.Floor;
import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.utils.CodeGeneratorUtil;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CodeGeneratorService {
    RedisTemplate<String, Object> redisTemplate;

    static SecureRandom random = new SecureRandom();
    static String REDIS_KEY_PREFIX = "codegen:";
    static String REDIS_KEY_FLOOR = "floor:";
    static String REDIS_KEY_ROOM = "room:";
    static String REDIS_KEY_TENANT = "tenant:";

    public String generateBuildingCode(User user) {
        String prefix = CodeGeneratorUtil.generatePrefixFromName(user.getFullName());
        String buildingCode;
        do {
            String randomNum = String.format("%06d", random.nextInt(1_000_000));
            buildingCode = prefix + randomNum;
        } while (Boolean.TRUE.equals(redisTemplate.hasKey(REDIS_KEY_PREFIX + "building:" + buildingCode)));

        redisTemplate.opsForValue().set(REDIS_KEY_PREFIX + "building:" + buildingCode, true);

        return buildingCode;
    }

    public String generateFloorName(String buildingId) {
        String redisKey = REDIS_KEY_PREFIX + REDIS_KEY_FLOOR + buildingId;
        long floorIndex = incrementRedisOrFail(redisKey);

        return "Tầng " + floorIndex;
    }

    public String generateRoomCode(Building building, Floor floor) {
        String prefix = CodeGeneratorUtil.getFirstCharPrefix(building.getBuildingName());
        Integer floorNumber = CodeGeneratorUtil.extractFloorNumber(floor.getNameFloor());

        if (floorNumber == null || floorNumber < 1) {
            throw new IllegalArgumentException("Tên tầng không hợp lệ: " + floor.getNameFloor());
        }

        String redisKey = REDIS_KEY_PREFIX + REDIS_KEY_ROOM + building.getId() + ":floor:" + floorNumber;
        long roomIndex = incrementRedisOrFail(redisKey);

        int roomNumber = floorNumber * 100 + (int) roomIndex;

        return prefix + roomNumber;
    }

    public String generateTenantCode(Building building) {
        String prefix = CodeGeneratorUtil.generatePrefixFromName(building.getBuildingName());
        String redisKey = REDIS_KEY_PREFIX + REDIS_KEY_TENANT + prefix;

        long index = incrementRedisOrFail(redisKey);

        long number = 100_000 + index - 1;

        return prefix + number;
    }

    private long incrementRedisOrFail(String key) {
        Long index = redisTemplate.opsForValue().increment(key, 1);
        if (index == null) {
            throw new IllegalStateException("Không thể tăng giá trị Redis: " + key);
        }
        return index;
    }
}
