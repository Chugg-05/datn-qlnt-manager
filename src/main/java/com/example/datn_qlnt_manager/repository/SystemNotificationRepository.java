package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.entity.SystemNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface SystemNotificationRepository extends JpaRepository<SystemNotification, String> {
    void deleteByUserId(String userId);

    Page<SystemNotification> findByUserId(String userId, Pageable pageable);

    Page<SystemNotification> findByUserIdAndIsReadFalse(String userId, Pageable pageable);

    Long countByUserIdAndIsReadFalse(String userId);

    @Modifying
    @Query("UPDATE SystemNotification sn SET sn.isRead = true WHERE sn.user.id = :userId")
    void markAllAsReadByUserId(@Param("userId") String userId);
}
