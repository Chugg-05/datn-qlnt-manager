package com.example.datn_qlnt_manager.service.strategy;

import com.example.datn_qlnt_manager.entity.Building;
import com.example.datn_qlnt_manager.utils.CodeGeneratorUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobCodeStrategy {

    public String generate(Building building) {
        String prefix = CodeGeneratorUtil.generatePrefixFromName(building.getBuildingName());

        LocalDate now = LocalDate.now();
        String year = String.format("%02d", now.getYear() % 100);
        String month = String.format("%02d", now.getMonthValue());

        String randomNumber = String.format("%04d", new SecureRandom().nextInt(10_000));

        return prefix + "CV" + year + month + randomNumber;
    }
}