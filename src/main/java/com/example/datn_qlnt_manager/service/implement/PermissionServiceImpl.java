package com.example.datn_qlnt_manager.service.implement;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.request.PermissionRequest;
import com.example.datn_qlnt_manager.dto.response.PermissionDetailResponse;
import com.example.datn_qlnt_manager.entity.Permission;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.PermissionMapper;
import com.example.datn_qlnt_manager.repository.PermissionRepository;
import com.example.datn_qlnt_manager.service.PermissionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionServiceImpl implements PermissionService {
    PermissionMapper permissionMapper;
    PermissionRepository permissionRepository;

    @Override
    public PaginatedResponse<PermissionDetailResponse> filterPermissions(String name, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Page<Permission> paging = permissionRepository.filterPermissionsPaging(name, pageable);

        List<PermissionDetailResponse> permissions = paging.getContent().stream()
                .map(permissionMapper::toPermissionDetailResponse)
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

        return PaginatedResponse.<PermissionDetailResponse>builder()
                .data(permissions)
                .meta(meta)
                .build();
    }

    @Override
    public PermissionDetailResponse createPermission(PermissionRequest request) {
        if (permissionRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.PERMISSION_EXISTED);
        }

        Permission permission = permissionMapper.toPermission(request);
        permission.setCreatedAt(Instant.now());
        permission.setUpdatedAt(Instant.now());
        permission = permissionRepository.save(permission);

        return permissionMapper.toPermissionDetailResponse(permission);
    }

    @Override
    public PermissionDetailResponse updatePermission(String permissionId, PermissionRequest request) {
        Permission permission = permissionRepository
                .findById(permissionId)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));

        if (!permission.getName().equals(request.getName()) && permissionRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.ROLE_EXISTED);
        }

        permissionMapper.updatePermission(request, permission);
        permission.setUpdatedAt(Instant.now());

        return permissionMapper.toPermissionDetailResponse(permissionRepository.save(permission));
    }

    @Override
    public void deletePermission(String permissionId) {
        permissionRepository.deleteById(permissionId);
    }
}
