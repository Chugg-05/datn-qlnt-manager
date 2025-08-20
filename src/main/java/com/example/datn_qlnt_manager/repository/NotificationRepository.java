package com.example.datn_qlnt_manager.repository;

import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.common.NotificationType;
import com.example.datn_qlnt_manager.dto.response.notification.NotificationResponse;
import com.example.datn_qlnt_manager.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    @Query("""
    SELECT new com.example.datn_qlnt_manager.dto.response.notification.NotificationResponse(
        n.id,
        n.title,
        n.content,
        n.image,
        n.notificationType,
        n.sendToAll,
        n.sentAt,
        u.id,
        u.fullName
    )
    FROM Notification n
    JOIN n.user u
    WHERE u.id = :userId
      AND (:query IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%'))
                        OR LOWER(n.content) LIKE LOWER(CONCAT('%', :query, '%')))
      AND (:type IS NULL OR n.notificationType = :type)
      AND (:from IS NULL OR n.sentAt >= :from)
      AND (:to IS NULL OR n.sentAt <= :to)
    ORDER BY n.sentAt DESC
""")
    Page<NotificationResponse> findAllByCurrentUserWithFilter(
            @Param("userId") String userId,
            @Param("query") String query,
            @Param("type") NotificationType type,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable);
}