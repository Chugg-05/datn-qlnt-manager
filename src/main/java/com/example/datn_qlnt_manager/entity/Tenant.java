package com.example.datn_qlnt_manager.entity;

import java.time.LocalDate;
import java.util.Set;

import com.example.datn_qlnt_manager.common.BuildingStatus;
import jakarta.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.datn_qlnt_manager.common.Gender;
import com.example.datn_qlnt_manager.common.TenantStatus;

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
@Table(name = "khach_thue")
public class Tenant extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chu_nha_id", nullable = false)
    User owner;

    @Column(name = "ma_khach_thue", nullable = false)
    String customerCode;

    @Column(name = "ho_ten", nullable = false)
    String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gioi_tinh", nullable = false)
    Gender gender;

    @Column(name = "ngay_sinh", nullable = false)
    @Temporal(TemporalType.DATE)
    LocalDate dob;

    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Column(name = "dien_thoai", nullable = false, unique = true)
    String phoneNumber;

    @Column(name = "so_cmnd", nullable = false)
    String identityCardNumber;

    @Column(name = "dia_chi", nullable = false)
    String address;

    @Column(name = "mat_truoc", nullable = false)
    String frontCCCD;

    @Column(name = "mat_sau", nullable = false)
    String backCCCD;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    TenantStatus tenantStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_truoc_do")
    TenantStatus previousTenantStatus;

    @Column(name = "co_tai_khoan", nullable = false)
    Boolean hasAccount;

    @OneToMany(mappedBy = "tenant", fetch = FetchType.LAZY)
    Set<ContractTenant> contractTenants;

    @Column(name = "ngay_xoa")
    LocalDate deletedAt;

}
