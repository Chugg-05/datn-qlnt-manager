package com.example.datn_qlnt_manager.service.implement;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.cloudinary.Cloudinary;
import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.NotificationFilter;
import com.example.datn_qlnt_manager.dto.request.notification.NotificationCreationRequest;
import com.example.datn_qlnt_manager.dto.request.notification.NotificationUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.notification.NotificationDetailResponse;
import com.example.datn_qlnt_manager.dto.response.notification.NotificationResponse;
import com.example.datn_qlnt_manager.entity.Notification;
import com.example.datn_qlnt_manager.entity.NotificationUser;
import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.NotificationMapper;
import com.example.datn_qlnt_manager.repository.NotificationRepository;
import com.example.datn_qlnt_manager.repository.NotificationUserRepository;
import com.example.datn_qlnt_manager.repository.UserRepository;
import com.example.datn_qlnt_manager.service.NotificationService;
import com.example.datn_qlnt_manager.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements NotificationService {

    NotificationUserRepository notificationUserRepository;
    NotificationRepository notificationRepository;
    UserRepository userRepository;
    UserService userService;
    NotificationMapper notificationMapper;
    Cloudinary cloudinary;

    @Override
    public NotificationResponse createNotification(NotificationCreationRequest request, MultipartFile image) {
        if (!Boolean.TRUE.equals(request.getSendToAll())
                && (request.getUsers() == null || request.getUsers().isEmpty())) {
            throw new AppException(ErrorCode.NOTIFICATION_USERS_REQUIRED);
        }

        if (!image.isEmpty()) {
            request.setImage(uploadImage(image));
        }

        Notification notification = notificationMapper.toNotification(request);
        notification.setSentAt(LocalDateTime.now());
        notification.setUser(userService.getCurrentUser());

        List<User> users = new ArrayList<>();
        if (Boolean.TRUE.equals(request.getSendToAll())) {
            users = userRepository.findAll();
        } else if (request.getUsers() != null) {
            users = userRepository.findAllById(request.getUsers());
        }
        notificationRepository.save(notification);

        List<NotificationUser> notifyLinks = users.stream()
                .map(user -> {
                    NotificationUser nu = new NotificationUser();
                    nu.setUser(user);
                    nu.setNotification(notification);
                    return nu;
                })
                .collect(Collectors.toList());

        notificationUserRepository.saveAll(notifyLinks);
        return notificationMapper.toResponse(notification);
    }

    @Override
    public NotificationResponse updateNotification(String notificationId, NotificationUpdateRequest request, MultipartFile image) {
        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!image.isEmpty()) {
            request.setImage(uploadImage(image));
        }

        if (!Boolean.TRUE.equals(request.getSendToAll())
                && (request.getUsers() == null || request.getUsers().isEmpty())) {
            throw new AppException(ErrorCode.NOTIFICATION_USERS_REQUIRED);
        }
        notificationMapper.updateNotificationFromRequest(request, notification);
        notification.setSentAt(LocalDateTime.now());

        notificationUserRepository.deleteByNotificationId(notificationId); // Xóa các liên kết cũ

        List<User> users = Boolean.TRUE.equals(request.getSendToAll())
                ? userRepository.findAll()
                : userRepository.findAllById(request.getUsers());

        List<NotificationUser> notifyLinks = users.stream()
                .map(user -> {
                    NotificationUser nu = new NotificationUser();
                    nu.setUser(user);
                    nu.setNotification(notification);
                    return nu;
                })
                .collect(Collectors.toList());

        notificationRepository.save(notification);
        notificationUserRepository.saveAll(notifyLinks);

        return notificationMapper.toResponse(notification);
    }

    @Override
    public PaginatedResponse<NotificationResponse> filterMyNotifications(
            NotificationFilter filter, int page, int size) {
        String userId = userService.getCurrentUser().getId();
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.DESC, "sentAt"));

        Page<NotificationResponse> pageResult = notificationRepository.findAllByCurrentUserWithFilter(
                userId,
                filter.getQuery(),
                filter.getNotificationType(),
                filter.getFromDate(),
                filter.getToDate(),
                pageable);

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(pageResult.getNumberOfElements())
                        .perPage(size)
                        .currentPage(page)
                        .totalPages(pageResult.getTotalPages())
                        .total(pageResult.getTotalElements())
                        .build())
                .build();

        return PaginatedResponse.<NotificationResponse>builder()
                .data(pageResult.getContent())
                .meta(meta)
                .build();
    }

    @Override
    @Transactional
    public NotificationDetailResponse getNotificationDetail(String notificationId) {
        User currentUser = userService.getCurrentUser();

        NotificationUser notificationUser = notificationUserRepository
                .findByNotificationIdAndUserId(notificationId, currentUser.getId())
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_ASSIGNED));

        Notification notification = notificationUser.getNotification();

        if (notificationUser.getIsRead() == null || notificationUser.getReadAt() == null) {
            notificationUser.setIsRead(true);
            notificationUser.setReadAt(LocalDateTime.now());
            notificationUserRepository.saveAndFlush(notificationUser);
        }
        return NotificationDetailResponse.builder()
                .notificationId(notification.getNotificationId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .notificationType(notification.getNotificationType())
                .sendToAll(notification.getSendToAll())
                .sentAt(notification.getSentAt())
                .fullName(notification.getUser().getFullName())
                .isRead(notificationUser.getIsRead())
                .readAt(notificationUser.getReadAt())
                .build();
    }

    @Override
    public void deleteNotificationById(String notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new AppException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }
        notificationRepository.deleteById(notificationId);
    }

    private String uploadImage(MultipartFile file) {
        try {
            @SuppressWarnings("unchecked") // bỏ qua cảnh báo
            Map<String, Object> uploadResult = (Map<String, Object>) cloudinary
                    .uploader()
                    .upload(
                            file.getBytes(),
                            Map.of("resource_type", "image", "upload_preset", "DATN_QLNT", "folder", "notification"));

            return (String) uploadResult.get("secure_url");
        } catch (IOException | AppException e) {
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }
    }
}
