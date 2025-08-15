package com.example.datn_qlnt_manager.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "thong_bao_he_thong")
public class SystemNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String systemNotificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "tieu_de", length = 255, nullable = false)
    private String title;

    @Column(name = "noi_dung", nullable = false)
    private String content;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "da_doc", nullable = false)
    private Boolean isRead;
}
