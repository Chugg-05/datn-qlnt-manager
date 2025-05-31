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
public abstract class AbstractEntity<T extends Serializable> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    T id;

    @Column(name = "created_at")
    @CreatedDate
    Instant createAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    Instant updateAt;
}
