package com.example.datn_qlnt_manager.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.common.Gender;
import com.example.datn_qlnt_manager.common.UserStatus;
import com.example.datn_qlnt_manager.dto.statistics.UserStatistics;
import com.example.datn_qlnt_manager.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query(
            """
		SELECT u FROM User u
		LEFT JOIN u.roles r
		WHERE (:fullName IS NULL OR u.fullName LIKE CONCAT('%', :fullName, '%'))
		AND (:email IS NULL OR u.email LIKE CONCAT('%', :email, '%'))
		AND (:phoneNumber IS NULL OR u.phoneNumber LIKE CONCAT('%', :phoneNumber, '%'))
		AND (:gender IS NULL OR u.gender = :gender)
		AND (:userStatus IS NULL OR u.userStatus = :userStatus)
		AND (:role IS NULL OR r.name = :role)
		ORDER BY u.updatedAt DESC
		""")
    Page<User> filterUsersPaging(
            @Param("fullName") String fullName,
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber,
            @Param("gender") Gender gender,
            @Param("userStatus") UserStatus userStatus,
            @Param("role") String role,
            Pageable pageable);

    // Lấy User kèm Roles và Permissions
    @Query("SELECT DISTINCT u FROM User u " + // DISTINCT: tránh bị nhân bản dòng khi join nhiều bảng
            "LEFT JOIN FETCH u.roles r "
            + "LEFT JOIN FETCH r.permissions "
            + "WHERE u.id = :userId")
    Optional<User> findUserWithRolesAndPermissionsById(@Param("userId") String userId);

    @Query("SELECT DISTINCT u FROM User u " + "LEFT JOIN FETCH u.roles r "
            + "LEFT JOIN FETCH r.permissions "
            + "WHERE u.email = :email")
    Optional<User> findWithRolesAndPermissionsByEmail(@Param("email") String email);

    Optional<User> findByEmail(String email); // tìm theo email

    @Query(
            """
		SELECT COUNT(u),
			SUM(CASE WHEN u.userStatus = 'ACTIVE' THEN 1 ELSE 0 END),
			SUM(CASE WHEN u.userStatus = 'EXPIRED' THEN 1 ELSE 0 END),
			SUM(CASE WHEN u.userStatus = 'LOCKED' THEN 1 ELSE 0 END),
			SUM(CASE WHEN u.userStatus = 'DELETED' THEN 1 ELSE 0 END)
		FROM User u
		WHERE u.id = :userId
	""")
    UserStatistics getTotalUsersByStatus(@Param("userId") String userId); // thống kê người dùng theo trạng thái

    boolean existsByEmail(String email); // ktra email đã tồn tại

    boolean existsByPhoneNumber(String phoneNumber); // ktra sđt đã tồn tại
}
