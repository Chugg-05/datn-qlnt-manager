package com.example.datn_qlnt_manager.entity;

import java.time.LocalDate;
import java.util.*;

import jakarta.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.datn_qlnt_manager.common.Gender;
import com.example.datn_qlnt_manager.common.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "user")
public class User extends AbstractEntity<String> implements UserDetails {
    @Column(name = "full_name", nullable = false)
    String fullName;

    @Column(name = "dob", nullable = false)
    LocalDate dob;

    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Column(name = "phone_number", nullable = false, unique = true)
    String phoneNumber;

    @Column(name = "profile_picture")
    String profilePicture;

    @JsonIgnore
    @Column(name = "refresh_token", columnDefinition = "TEXT")
    String refreshToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    UserStatus userStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    Gender gender;

    @JsonIgnore // tránh rò rỉ khi trả json về client
    @Column(name = "password", nullable = false)
    String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .toList();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.userStatus.equals(UserStatus.LOCKED);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return this.userStatus.equals(UserStatus.ACTIVE);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                fullName, dob, email, phoneNumber, profilePicture, refreshToken, userStatus, gender, password);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        User user = (User) object;

        return Objects.equals(fullName, user.fullName)
                && Objects.equals(dob, user.dob)
                && Objects.equals(email, user.email)
                && Objects.equals(phoneNumber, user.phoneNumber)
                && Objects.equals(profilePicture, user.profilePicture)
                && Objects.equals(refreshToken, user.refreshToken)
                && userStatus == user.userStatus
                && gender == user.gender
                && Objects.equals(password, user.password);
    }
}
/*
1. isAccountNonExpired() --> Kiểm tra xem tài khoản của người dùng có bị hết hạn hay không.
						--> Một tài khoản hết hạn thường được sử dụng để vô hiệu hóa người dùng sau một
							khoảng thời gian nhất định.

2.isAccountNonLocked() --> Kiểm tra xem tài khoản của người dùng có bị khóa hay không.
					--> Thường được sử dụng để tạm thời vô hiệu hóa tài khoản của người dùng nếu có hành
						vi đáng ngờ, như nhập sai mật khẩu nhiều lần.

3.isCredentialsNonExpired() --> Kiểm tra xem thông tin xác thực (mật khẩu) của người dùng có bị hết hạn hay không.
							--> Thường được sử dụng khi bạn muốn yêu cầu người dùng thay đổi mật khẩu sau một
								khoảng thời gian.

4.isEnabled --> được sử dụng để kiểm tra xem tài khoản của người dùng có được kích hoạt hay không.
			--> nếu Khi phương thức này trả về false, Spring Security sẽ ngăn người dùng đăng nhập,
				ngay cả khi họ nhập đúng tên đăng nhập và mật khẩu.
*/
