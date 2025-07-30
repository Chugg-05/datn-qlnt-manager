package com.example.datn_qlnt_manager.utils;

import com.example.datn_qlnt_manager.common.InvoiceStatus;
import com.example.datn_qlnt_manager.common.InvoiceType;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
@UtilityClass
public class FormatUtil {

    private static final Locale VIETNAM_LOCALE = Locale.forLanguageTag("vi-VN");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    public static String formatCurrency(Number amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(VIETNAM_LOCALE);
        return formatter.format(amount); // Ví dụ: 3.200.000 ₫
    }

    public static String formatNumber(Number number) {
        NumberFormat formatter = NumberFormat.getNumberInstance(VIETNAM_LOCALE);
        return formatter.format(number); // Ví dụ: 3.200.000
    }

    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
    }

    public static String formatInvoiceType(InvoiceType type) {
        return switch (type) {
            case HANG_THANG -> "Hóa đơn hàng tháng";
            case CUOI_CUNG -> "Hóa đơn thanh lý";
        };
    }

    public static String formatInvoiceStatus(InvoiceStatus status) {
        return switch (status) {
            case CHO_THANH_TOAN -> "Chờ thanh toán";
            case DA_THANH_TOAN -> "Đã thanh toán";
            case QUA_HAN -> "Quá hạn";
            case CHUA_THANH_TOAN -> "Chưa thanh toán";
            case HUY -> "Đã hủy";
        };
    }

}
