package com.example.datn_qlnt_manager.repository;

import java.util.List;
import java.util.Optional;

import com.example.datn_qlnt_manager.dto.response.notification.SentToUsers;
import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.entity.NotificationUser;

@Repository
public interface NotificationUserRepository extends JpaRepository<NotificationUser, String> {
    @Query("SELECT nu FROM NotificationUser nu " + "JOIN FETCH nu.notification n "
            + "WHERE n.notificationId = :notificationId AND nu.user.id = :userId")
    Optional<NotificationUser> findByNotificationIdAndUserId(
            @Param("notificationId") String notificationId, @Param("userId") String userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM NotificationUser nu WHERE nu.notification.notificationId = :notificationId")
    void deleteByNotificationId(@Param("notificationId") String notificationId);

    @Query("""
        SELECT new com.example.datn_qlnt_manager.dto.response.notification.SentToUsers(
            u.id, u.fullName
        )
        FROM NotificationUser nu
        JOIN nu.user u
        WHERE nu.notification.notificationId = :notificationId
    """)
    List<SentToUsers> findRecipients(@Param("notificationId") String notificationId);
}
