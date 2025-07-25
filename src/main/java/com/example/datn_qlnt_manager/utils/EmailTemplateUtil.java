package com.example.datn_qlnt_manager.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;

import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class EmailTemplateUtil {
    private static final String TEMPLATE_FOLDER = "templates/email/";

    public String loadTemplate(String templateName, Map<String, String> placeholders) {
        try {
            String fileName = templateName.endsWith(".html") ? templateName : templateName + ".html";

            ClassPathResource resource = new ClassPathResource(TEMPLATE_FOLDER + fileName);
            String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }

            return content;

        } catch (IOException e) {
            log.error("Không thể đọc file template: {}", templateName, e);
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
