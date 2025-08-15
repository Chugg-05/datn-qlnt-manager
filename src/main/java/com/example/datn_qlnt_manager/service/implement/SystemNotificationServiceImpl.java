package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.response.systemnotification.SystemNotificationResponse;
import com.example.datn_qlnt_manager.entity.SystemNotification;
import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.SystemNotificationMapper;
import com.example.datn_qlnt_manager.repository.SystemNotificationRepository;
import com.example.datn_qlnt_manager.repository.UserRepository;
import com.example.datn_qlnt_manager.service.SystemNotificationService;
import com.example.datn_qlnt_manager.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SystemNotificationServiceImpl implements SystemNotificationService {

     SystemNotificationRepository systemNotificationRepository;
     UserRepository userRepository;
     UserService userService;
    SystemNotificationMapper systemNotificationMapper;

    @Override
    public SystemNotificationResponse createNotification(String userId, String title, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        SystemNotification notification = SystemNotification.builder()
                .user(user)
                .title(title)
                .content(content)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        SystemNotification saved = systemNotificationRepository.save(notification);

        return SystemNotificationResponse.builder()
                .systemNotificationId(saved.getSystemNotificationId())
                .userId(saved.getUser().getId())
                .title(saved.getTitle())
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt())
                .isRead(saved.getIsRead())
                .build();
    }

    @Override
    public void deleteNotification(String systemNotificationId) {
        if (!systemNotificationRepository.existsById(systemNotificationId)) {
            throw new AppException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }
        systemNotificationRepository.deleteById(systemNotificationId);
    }

    @Transactional
    @Override
    public void deleteAllNotificationsByUser(String userId) {
        systemNotificationRepository.deleteByUserId(userId);
    }

    @Override
    public PaginatedResponse<SystemNotificationResponse> getNotificationsByCurrentUser(int page, int size) {
        String currentUserId = userService.getCurrentUser().getId();
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<SystemNotification> pageResult = systemNotificationRepository.findByUserId(currentUserId, pageable);
        return buildSystemNotificationPage(pageResult, page, size);
    }

    @Override
    public PaginatedResponse<SystemNotificationResponse> getUnreadNotificationsByCurrentUser(int page, int size) {
        String currentUserId = userService.getCurrentUser().getId();
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<SystemNotification> pageResult = systemNotificationRepository.findByUserIdAndIsReadFalse(currentUserId, pageable);
        return buildSystemNotificationPage(pageResult, page, size);
    }

    private PaginatedResponse<SystemNotificationResponse> buildSystemNotificationPage(
            Page<SystemNotification> pageResult, int page, int size) {

        List<SystemNotificationResponse> responseList = pageResult.getContent()
                .stream()
                .map(systemNotificationMapper::toResponse)
                .toList();

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(pageResult.getNumberOfElements())
                        .perPage(size)
                        .currentPage(page)
                        .totalPages(pageResult.getTotalPages())
                        .total(pageResult.getTotalElements())
                        .build())
                .build();

        return PaginatedResponse.<SystemNotificationResponse>builder()
                .data(responseList)
                .meta(meta)
                .build();
    }


    @Override
    public Long countUnreadNotificationsByUser(String userId) {
        return systemNotificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    @Override
    public void markNotificationAsRead(String systemNotificationId) {
        String currentUserId = userService.getCurrentUser().getId();

        SystemNotification notification = systemNotificationRepository.findById(systemNotificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getUser().getId().equals(currentUserId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        notification.setIsRead(true);
        systemNotificationRepository.save(notification);
    }

    @Transactional
    @Override
    public void markAllNotificationsAsReadByCurrentUser() {
        String currentUserId = userService.getCurrentUser().getId();
        systemNotificationRepository.markAllAsReadByUserId(currentUserId);
    }
}
