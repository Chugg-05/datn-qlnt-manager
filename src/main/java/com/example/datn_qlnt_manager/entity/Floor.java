package com.example.datn_qlnt_manager.entity;

import jakarta.persistence.*;

import com.example.datn_qlnt_manager.common.FloorStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "tang")
public class Floor extends AbstractEntity {

    @Column(name = "ten_tang", unique = true, nullable = false)
    private String nameFloor;

    @Column(name = "trang_thai", nullable = false)
    @Enumerated(EnumType.STRING)
    private FloorStatus status;

    @Column(name = "mo_ta")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toa_nha_id", nullable = false)
    private Building building;
}
