package com.example.datn_qlnt_manager.entity;

import jakarta.persistence.*;

import com.example.datn_qlnt_manager.common.FeedbackStatus;
import com.example.datn_qlnt_manager.common.FeedbackType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@Table(name = "phan_hoi_va_ho_tro")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Feedback extends AbstractEntity {

    @Column(name = "ten_khach", nullable = false)
    String nameSender;

    @Column(name = "ma_phong", nullable = false)
    String roomCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_phan_hoi", nullable = false)
    FeedbackType feedbackType;

    @Column(name = "ten_phan_hoi", nullable = false)
    String feedbackName;

    @Column(name = "noi_dung", nullable = false)
    String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    FeedbackStatus feedbackStatus;

    @Column(name = "danh_gia")
    Integer rating;

    @Column(name = "tep_dinh_kem")
    String attachment;

    @Column(name = "ly_do_tu_choi")
    String rejectionReason;
}
