package com.example.datn_qlnt_manager.entity;

import java.time.Instant;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "thong_bao_nguoi_dung")
public class NotificationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    String notificationUserId;

    @ManyToOne
    @JoinColumn(name = "thong_bao_id")
    Notification notification;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "da_doc")
    Boolean isRead;

    @Column(name = "thoi_gian_doc")
    Instant readAt;
}
