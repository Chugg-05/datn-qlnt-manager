package com.example.datn_qlnt_manager.service.implement;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.request.RoleRequest;
import com.example.datn_qlnt_manager.dto.response.RoleDetailResponse;
import com.example.datn_qlnt_manager.entity.Role;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.RoleMapper;
import com.example.datn_qlnt_manager.repository.PermissionRepository;
import com.example.datn_qlnt_manager.repository.RoleRepository;
import com.example.datn_qlnt_manager.service.RoleService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceImpl implements RoleService {
    RoleMapper roleMapper;
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;

    @Override
    public PaginatedResponse<RoleDetailResponse> filterRoles(String name, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Page<Role> paging = roleRepository.filterRolesPaging(name, pageable);

        List<RoleDetailResponse> roles = paging.getContent().stream()
                .map(roleMapper::toRoleDetailResponse)
                .toList();

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(paging.getNumberOfElements())
                        .perPage(size)
                        .currentPage(page)
                        .totalPages(paging.getTotalPages())
                        .total(paging.getTotalElements())
                        .build())
                .build();

        return PaginatedResponse.<RoleDetailResponse>builder()
                .data(roles)
                .meta(meta)
                .build();
    }

    @Override
    public RoleDetailResponse createRole(RoleRequest request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.ROLE_EXISTED);
        }

        var role = roleMapper.toRole(request);
        var permission = permissionRepository.findAllById(request.getPermissions());

        role.setPermissions(new HashSet<>(permission));
        role.setCreatedAt(Instant.now());
        role.setUpdatedAt(Instant.now());
        role = roleRepository.save(role);

        return roleMapper.toRoleDetailResponse(role);
    }

    @Override
    public RoleDetailResponse updateRole(String roleId, RoleRequest request) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        if (!role.getName().equals(request.getName()) && roleRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.ROLE_EXISTED);
        }

        roleMapper.updateRole(request, role);

        var permissions = permissionRepository.findAllById(request.getPermissions());

        role.setPermissions(new HashSet<>(permissions));
        role.setUpdatedAt(Instant.now());

        return roleMapper.toRoleDetailResponse(roleRepository.save(role));
    }

    @Override
    public void deleteRole(String roleId) {
        roleRepository.deleteById(roleId);
    }
}
