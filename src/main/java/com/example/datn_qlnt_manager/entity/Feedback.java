package com.example.datn_qlnt_manager.entity;

import com.example.datn_qlnt_manager.common.FeedbackStatus;
import com.example.datn_qlnt_manager.common.FeedbackType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@Table(name = "phan_hoi")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Feedback extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_thue_id", nullable = false)
     Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_id", nullable = false)
     Room room;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_phan_hoi", nullable = false)
     FeedbackType feedbackType;

    @Column(name = "noi_dung", nullable = false)
     String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
     FeedbackStatus feedbackStatus;

    @Column(name = "danh_gia")
     Integer rating;

    @Column(name = "tep_dinh_kem")
     String attachment;
}