package com.example.datn_qlnt_manager.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chi_so_cong_to")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeterReading extends AbstractEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cong_to_id", nullable = false)
    Meter meter;

    @Column(name = "chi_so_cu", nullable = false)
    Integer previousIndex;

    @Column(name = "chi_so_moi", nullable = false)
    Integer currentIndex;

    @Column(name = "so_luong", nullable = false)
    Integer quantity;

    @Column(name = "thang", nullable = false)
    Integer month;

    @Column(name = "nam", nullable = false)
    Integer year;

    @Column(name = "ngay_doc")
    LocalDateTime readingDate;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    String description;


}

