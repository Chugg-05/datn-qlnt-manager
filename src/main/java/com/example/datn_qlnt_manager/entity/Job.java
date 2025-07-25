package com.example.datn_qlnt_manager.entity;

import java.util.Date;

import jakarta.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.datn_qlnt_manager.common.JobObjectType;
import com.example.datn_qlnt_manager.common.JobPriorityLevel;
import com.example.datn_qlnt_manager.common.JobStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "cong_viec")
public class Job extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "toa_nha_id")
    Building building;

    @Column(name = "ma_cong_viec")
    String jobCode;

    @Column(name = "tieu_de")
    String title;

    @Column(name = "mo_ta")
    String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "muc_do_uu_tien")
    JobPriorityLevel jobPriorityLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    JobStatus jobStatus;

    @Column(name = "han_hoan_thanh")
    Date completionDeadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_doi_tuong")
    JobObjectType jobObjectType;
}
