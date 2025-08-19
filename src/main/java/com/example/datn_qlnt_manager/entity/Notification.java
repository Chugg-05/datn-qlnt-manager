package com.example.datn_qlnt_manager.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import com.example.datn_qlnt_manager.common.NotificationType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "thong_bao")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    String notificationId;

    @Column(name = "tieu_de")
    String title;

    @Column(name = "noi_dung")
    String content;

    @Column(name = "gui_toi_tat_ca")
    Boolean sendToAll;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_thong_bao")
    NotificationType notificationType;

    @Column(name = "ngay_gui")
    LocalDateTime sentAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

}
