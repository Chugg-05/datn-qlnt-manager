package com.example.datn_qlnt_manager.entity;

import com.example.datn_qlnt_manager.common.ServiceRoomStatus;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "dich_vu_phong", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"phong_id", "dich_vu_id"})
})
public class ServiceRoom extends AbstractEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dich_vu_id", nullable = false)
     Service service;

    @Column(name = "ma_su_dung", nullable = false)
     String usageCode;

    // thời điểm gán dịch vụ vào phòng
    @Column(name = "thoi_gian_ap_dung", nullable = false)
    LocalDateTime applyTime;

    // ngày bắt đầu tính phí dịch vụ
    @Column(name = "ngay_bat_dau", nullable = false)
     LocalDate startDate;

    @Column(name = "tong_tien", nullable = false)
    BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    ServiceRoomStatus serviceRoomStatus;

    @Column(name = "mo_ta")
     String descriptionServiceRoom;
}
