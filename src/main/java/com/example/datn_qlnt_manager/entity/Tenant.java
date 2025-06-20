package com.example.datn_qlnt_manager.entity;

import com.example.datn_qlnt_manager.common.Gender;
import com.example.datn_qlnt_manager.common.TenantStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "khach_thue")
public class Tenant extends AbstractEntity {

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = true)
        User user;

        @Column(name = "ma_khach_thue", nullable = false, unique = true)
        String customerCode;

        @Column(name = "ho_ten", nullable = false)
        String fullName;

        @Enumerated(EnumType.STRING)
        @Column(name = "gioi_tinh", nullable = false)
        Gender gender;

        @Column(name = "ngay_sinh", nullable = false)
        @Temporal(TemporalType.DATE)
        Date dob;

        @Column(name = "so_cmnd", nullable = false)
        String identityCardNumber;

        @Column(name = "dien_thoai", nullable = false, unique = true)
        String phoneNumber;

        @Column(name = "email", nullable = false, unique = true)
        String email;

        @Column(name = "dia_chi", nullable = false)
        String address;

        @Enumerated(EnumType.STRING)
        @Column(name = "trang_thai", nullable = false)
        TenantStatus tenantStatus;

        @Column(name = "la_dai_dien", nullable = false)
        Boolean isRepresentative;

        @Column(name = "co_tai_khoan", nullable = false)
        Boolean hasAccount;
}
