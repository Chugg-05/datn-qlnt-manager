package com.example.datn_qlnt_manager.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chi_so")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeterReading extends AbstractEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cong_to_id", nullable = false)
    Meter meter;

    @Column(name = "chi_so_cu", nullable = false)
    Integer oldIndex;

    @Column(name = "chi_so_moi", nullable = false)
    Integer newIndex;

    @Column(name = "so_luong", insertable = false, updatable = false)
    Integer quantity;

    @Column(name = "thang", nullable = false)
    Integer month;

    @Column(name = "nam", nullable = false)
    Integer year;

    @Column(name = "ngay_doc")
    LocalDate readingDate;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    String descriptionMeterReading;


}

