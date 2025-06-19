package com.example.datn_qlnt_manager.configuration;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.datn_qlnt_manager.common.Gender;
import com.example.datn_qlnt_manager.common.UserStatus;
import com.example.datn_qlnt_manager.constant.PredefinedPermission;
import com.example.datn_qlnt_manager.constant.PredefinedRole;
import com.example.datn_qlnt_manager.constant.PredefinedRolePermissionMapping;
import com.example.datn_qlnt_manager.entity.Permission;
import com.example.datn_qlnt_manager.entity.Role;
import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.repository.PermissionRepository;
import com.example.datn_qlnt_manager.repository.RoleRepository;
import com.example.datn_qlnt_manager.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationConfig {
    PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${admin.email}")
    String adminEmail;

    @NonFinal
    @Value("${admin.password}")
    String adminPassword;

    @Bean
    @ConditionalOnProperty( // Kiểm tra nếu driver MySQL được sử dụng
            prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner( // Chỉ chạy khi sử dụng MySQL
            UserRepository userRepository, RoleRepository roleRepository, PermissionRepository permissionRepository) {
        return args -> { // Hàm này sẽ chạy khi ứng dụng khởi động
            log.info("🔧 Starting application initialization...");

            // Tạo Permission nếu chưa tồn tại
            Map<String, String> predefinedPermissions = Map.of(
                    PredefinedPermission.READ, "Quyền xem dữ liệu",
                    PredefinedPermission.WRITE, "Quyền tạo mới dữ liệu",
                    PredefinedPermission.EDIT, "Quyền chỉnh sửa dữ liệu",
                    PredefinedPermission.DELETE, "Quyền xóa dữ liệu");

            // Lặp qua các permission đã định nghĩa và lưu vào database nếu chưa tồn tại
            predefinedPermissions.forEach((name, description) -> {
                if (!permissionRepository.existsByName(name)) {
                    permissionRepository.save(Permission.builder()
                            .name(name)
                            .description(description)
                            .build());
                }
            });

            // Lấy tất cả Permission đã lưu trong database
            Map<String, Permission> permissionMap = new HashMap<>();
            permissionRepository.findAll().forEach(p -> permissionMap.put(p.getName(), p));

            // Tạo Role nếu chưa tồn tại
            Map<String, String> predefinedRoles = Map.of(
                    PredefinedRole.ADMIN_ROLE, "Admin role",
                    PredefinedRole.MANAGER_ROLE, "Manager role",
                    PredefinedRole.STAFF_ROLE, "Staff role",
                    PredefinedRole.USER_ROLE, "User role");

            // Lặp qua các role đã định nghĩa và lưu vào database nếu chưa tồn tại
            predefinedRoles.forEach((roleName, description) -> {
                Role role = roleRepository
                        .findByName(roleName)
                        .orElseGet(() -> roleRepository.save(Role.builder()
                                .name(roleName)
                                .description(description)
                                .build()));

                // Lấy danh sách Permission tương ứng với Role và gán vào Role
                List<String> permissionNames = PredefinedRolePermissionMapping.getPermissionsForRole(roleName);
                Set<Permission> assignedPermissions = new HashSet<>(); // Khởi tạo Set để lưu các Permission đã gán

                // Lặp qua danh sách Permission và thêm vào Set nếu tồn tại trong permissionMap
                for (String permissionName : permissionNames) {
                    Permission p = permissionMap.get(permissionName);
                    if (p != null) assignedPermissions.add(p);
                }

                // Gán danh sách Permission đã gán vào Role và lưu Role
                role.setPermissions(assignedPermissions);
                roleRepository.save(role);
            });

            Optional<User> existingAdmin = userRepository.findByEmail(adminEmail);

            // Kiểm tra xem đã có user admin chưa
            if (existingAdmin.isEmpty()) {
                Role adminRole = roleRepository
                        .findByName(PredefinedRole.ADMIN_ROLE)
                        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

                var roles = new HashSet<Role>();
                roles.add(adminRole);

                User admin = User.builder()
                        .fullName("Admin")
                        .gender(Gender.MALE)
                        .dob(LocalDate.parse("2000-01-01"))
                        .email(adminEmail)
                        .profilePicture(null)
                        .phoneNumber("0325454545")
                        .password(passwordEncoder.encode(adminPassword))
                        .userStatus(UserStatus.ACTIVE)
                        .refreshToken(null)
                        .roles(roles)
                        .build();

                admin.setCreatedAt(Instant.now());
                admin.setUpdatedAt(Instant.now());

                userRepository.save(admin);
                log.info("Default admin user created.");
            } else {
                log.info("Admin user already exists.");
            }

            log.info("Application initialization completed...");
        };
    }
}
