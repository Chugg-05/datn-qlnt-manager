package com.example.datn_qlnt_manager.service.strategy;

import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.service.RedisService;
import com.example.datn_qlnt_manager.utils.CodeGeneratorUtil;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TenantCodeStrategy {

    RedisService redisService;

    public String generate(User user) {
        String prefix = CodeGeneratorUtil.generatePrefixFromName(user.getFullName());
        String redisKey = "codegen:tenant:" + user.getId();

        long index = redisService.increment(redisKey);
        long number = 100_000 + index - 1;

        return  "KH" + prefix + String.format("%06d", number);
    }
}
