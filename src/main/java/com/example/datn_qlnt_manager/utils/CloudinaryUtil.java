package com.example.datn_qlnt_manager.utils;

import com.cloudinary.Cloudinary;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryUtil {

    Cloudinary cloudinary;

    public String uploadImage(MultipartFile file, String nameFolder) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader()
                    .upload(file.getBytes(),
                            Map.of("resource_type", "image", "upload_preset", "DATN_QLNT", "folder", nameFolder));
            return (String) uploadResult.get("secure_url");
        } catch (IOException | AppException e) {
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }
    }
}
