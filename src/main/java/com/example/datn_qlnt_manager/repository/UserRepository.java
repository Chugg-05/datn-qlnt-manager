package com.example.datn_qlnt_manager.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // Lấy User kèm Roles và Permissions
    @Query("SELECT DISTINCT u FROM User u " + // DISTINCT: tránh bị nhân bản dòng khi join nhiều bảng
            "LEFT JOIN FETCH u.roles r "
            + "LEFT JOIN FETCH r.permissions "
            + "WHERE u.id = :userId")
    Optional<User> findUserWithRolesAndPermissionsById(@Param("userId") String userId);

    @Query("SELECT DISTINCT u FROM User u " + "LEFT JOIN FETCH u.roles "
            + // LEFT JOIN FETCH: cho phép lấy những User không có role
            "WHERE (LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%')) "
            + "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) "
            + "OR LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :query, '%'))) "
            + "AND u.id != :currentUserId "
            + "AND u.userStatus = 'ACTIVE'")
    Page<User> searchUser(
            @Param("query") String query, @Param("currentUserId") String currentUserId, Pageable pageable);

    @Query("SELECT DISTINCT u FROM User u " + "LEFT JOIN FETCH u.roles r "
            + "LEFT JOIN FETCH r.permissions "
            + "WHERE u.email = :email")
    Optional<User> findWithRolesAndPermissionsByEmail(@Param("email") String email);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
