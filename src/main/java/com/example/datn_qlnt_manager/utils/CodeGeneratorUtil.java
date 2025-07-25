package com.example.datn_qlnt_manager.utils;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CodeGeneratorUtil {

    public static final Set<String> IGNORED_WORDS = Set.of("TOA", "BLOCK", "DAY", "KHU", "NHA");
    private static final Pattern FLOOR_PATTERN = Pattern.compile("Tầng\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

    public String removeVietnameseDiacritics(String input) {
        if (input == null) {
            return null;
        }

        // Chuyển đổi sang dạng chuẩn NFD (Normalization Form D)
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        // Biểu thức chính quy để tìm các dấu kết hợp
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

        // Loại bỏ các ký tự không phải chữ cái, số và khoảng trắng
        return pattern.matcher(normalized).replaceAll("").replaceAll("[^\\p{L}\\p{Nd}\\s]", "");
    }

    public String generatePrefixFromName(String name) {
        // Kiểm tra nếu tên là null hoặc rỗng thì trả về mặc định "XX"
        if (name == null || name.isBlank()) {
            return "XX";
        }

        // Loại bỏ dấu tiếng Việt và chuyển sang chữ hoa
        String cleanedName = removeVietnameseDiacritics(name).toUpperCase().trim();

        // Tách tên thành các phần dựa trên khoảng trắng
        String[] parts = cleanedName.split("\\s+");

        // Lọc bỏ từ vô nghĩa
        List<String> validWords = Arrays.stream(parts)
                .filter(part -> part != null && !part.isBlank() && !IGNORED_WORDS.contains(part))
                .toList();

        if (validWords.isEmpty()) {
            return "XX";
        }

        // Lấy ký tự đầu + cuối nếu có >=2 từ
        if (validWords.size() >= 2) {
            char first = validWords.getFirst().charAt(0);
            char last = validWords.getLast().charAt(0);
            return String.valueOf(first) + last;
        }

        // Nếu chỉ có 1 từ, lấy 2 ký tự đầu tiên (nếu có)
        String word = validWords.getFirst();
        return word.length() >= 2
                ? word.substring(0, 2)
                : String.format("%-2s", word).replace(' ', 'X');
    }

    public String getFirstCharPrefix(String name) {
        if (name == null || name.isBlank()) {
            return "X";
        }

        String cleanedName = removeVietnameseDiacritics(name).toUpperCase().trim();
        return cleanedName.isEmpty() ? "X" : String.valueOf(cleanedName.charAt(0));
    }

    public static Integer extractFloorNumber(String tenTang) {
        if (tenTang == null) return null;

        Matcher matcher = FLOOR_PATTERN.matcher(tenTang.trim());

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return null;
    }

    public String generateInvoiceCode(String roomCode, int month, int year) {
        String prefix = "HD";
        String yy = String.format("%02d", year % 100);
        String mm = String.format("%02d", month);
        String randomNumber = String.format("%04d", new SecureRandom().nextInt(10_000));
        return prefix + roomCode + yy + mm + randomNumber;
    }
}
