package com.example.datn_qlnt_manager.utils;


import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class PasswordUtil {
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "@#$%&*!";
    private static final String ALL = UPPER + LOWER + DIGITS + SPECIAL;

    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomPassword() {
        List<Character> passwordChars = new ArrayList<>();

        // Bắt buộc có mỗi loại ít nhất 1 ký tự
        passwordChars.add(randomChar(UPPER));
        passwordChars.add(randomChar(LOWER));
        passwordChars.add(randomChar(DIGITS));
        passwordChars.add(randomChar(SPECIAL));

        // Bổ sung thêm 4 ký tự bất kỳ
        for (int i = 0; i < 4; i++) {
            passwordChars.add(randomChar(ALL));
        }

        // Trộn ngẫu nhiên để tránh predictable pattern
        Collections.shuffle(passwordChars, random);

        // Convert list thành chuỗi
        StringBuilder password = new StringBuilder();
        for (char c : passwordChars) {
            password.append(c);
        }

        return password.toString();
    }

    private static char randomChar(String chars) {
        return chars.charAt(random.nextInt(chars.length()));
    }
}
