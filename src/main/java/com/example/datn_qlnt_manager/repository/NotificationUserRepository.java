package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.entity.NotificationUser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface NotificationUserRepository extends JpaRepository<NotificationUser, String> {
    @Query("SELECT nu FROM NotificationUser nu " +
            "JOIN FETCH nu.notification n " +
            "WHERE n.notificationId = :notificationId AND nu.user.id = :userId")
    Optional<NotificationUser> findByNotificationIdAndUserId(@Param("notificationId") String notificationId, @Param("userId") String userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM NotificationUser nu WHERE nu.notification.notificationId = :notificationId")
    void deleteByNotificationId(@Param("notificationId") String notificationId);
}
