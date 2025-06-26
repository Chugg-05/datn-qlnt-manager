package com.example.datn_qlnt_manager.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeGeneratorUtil {

    public static final Set<String> IGNORED_WORDS = Set.of("TOA", "BLOCK", "DAY", "KHU", "NHA");
    private static final Pattern FLOOR_PATTERN = Pattern.compile("Tầng\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

    public static String removeVietnameseDiacritics(String input) {
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

    public static String generatePrefixFromName(String name) {
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

    public static String getFirstCharPrefix(String name) {
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

    // mã tự sinh
    public static String generateSecureCode(String prefix) {
        String raw = LocalDateTime.now().toString() + UUID.randomUUID();
        String hash = sha256(raw).substring(0, 8).toUpperCase();
        return prefix + "_" + hash;
    }

    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported");
        }
    }
}
