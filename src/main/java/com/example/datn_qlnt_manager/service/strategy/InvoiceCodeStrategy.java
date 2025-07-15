package com.example.datn_qlnt_manager.service.strategy;

import com.example.datn_qlnt_manager.entity.Room;
import com.example.datn_qlnt_manager.service.RedisService;
import com.example.datn_qlnt_manager.utils.CodeGeneratorUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceCodeStrategy {
    RedisService redisService;

    public String generate(Room room, int month, int year) {
        String code;
        do {
            code = CodeGeneratorUtil.generateInvoiceCode(room.getRoomCode(), month, year);
        } while (redisService.exists("codegen:invoice:" + code));

        redisService.markAsUsed("codegen:invoice:" + code);

        return code;
    }
}
