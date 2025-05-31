package com.example.datn_qlnt_manager.entity;

import jakarta.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@Table(name = "permission")
public class Permission extends AbstractEntity<String> {
    @Column(name = "name", nullable = false, unique = true)
    String name;

    @Column(name = "description", nullable = false)
    String description;
}
