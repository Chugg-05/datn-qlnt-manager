package com.example.datn_qlnt_manager.entity;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
// class này định nghĩa ctruc chung cho tất cả các entity
public abstract class AbstractEntity
        implements Serializable { // 'implements Serializable' tránh lỗi runtime khi truyền/ lưu entity (cho phép chuyển
    // đối tượng thành luồng byte)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "created_at")
    @CreatedDate // tự động set khi add
    Instant createAt;

    @Column(name = "updated_at")
    @LastModifiedDate // tự động update thời gian khi Update
    Instant updateAt;
}
