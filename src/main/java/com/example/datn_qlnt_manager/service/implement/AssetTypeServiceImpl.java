package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.AssetTypeFilter;
import com.example.datn_qlnt_manager.dto.request.assetType.AssetTypeCreationRequest;
import com.example.datn_qlnt_manager.dto.request.assetType.AssetTypeUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.assetType.AssetTypeResponse;
import com.example.datn_qlnt_manager.entity.AssetType;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.AssetTypeMapper;
import com.example.datn_qlnt_manager.repository.AssetTypeRepository;
import com.example.datn_qlnt_manager.service.AssetTypeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssetTypeServiceImpl implements AssetTypeService {

    AssetTypeRepository assetTypeRepository;
    AssetTypeMapper assetTypeMapper;

    @Override
    public AssetTypeResponse createAssetType(AssetTypeCreationRequest request) {
        boolean exists = assetTypeRepository.existsByNameAssetTypeAndAssetGroup(
                request.getNameAssetType(), request.getAssetGroup()
        );
        if (exists) {
            throw new AppException(ErrorCode.ASSET_TYPE_EXISTED);
        }

        AssetType assetType = assetTypeMapper.toAssetType(request);
        assetType.setCreatedAt(Instant.now());
        assetType.setUpdatedAt(Instant.now());

        return assetTypeMapper.toResponse(assetTypeRepository.save(assetType));
    }

    @Override
    public PaginatedResponse<AssetTypeResponse> getAssetTypes(AssetTypeFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by("nameAssetType").descending());

        Page<AssetType> assetPage = assetTypeRepository.filterAssetTypesPaging(
                filter.getNameAssetType(),
                filter.getAssetGroup(),
                pageable
        );

        List<AssetTypeResponse> responses = assetPage.getContent().stream()
                .map(assetTypeMapper::toResponse)
                .toList();

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(assetPage.getNumberOfElements())
                        .perPage(size)
                        .currentPage(page)
                        .totalPages(assetPage.getTotalPages())
                        .total(assetPage.getTotalElements())
                        .build())
                .build();

        return PaginatedResponse.<AssetTypeResponse>builder()
                .data(responses)
                .meta(meta)
                .build();
    }

    @Override
    public AssetTypeResponse updateAssetType(String assetTypeId, AssetTypeUpdateRequest request) {
       AssetType assetType = assetTypeRepository.findById(assetTypeId).orElseThrow(() -> new AppException(ErrorCode.ASSSET_TYPE_NOT_FOUND));

       assetTypeRepository.findByNameAssetTypeAndAssetGroupAndIdNot(request.getNameAssetType(), assetType.getAssetGroup(), assetTypeId)
               .ifPresent(existingAssetType -> {
                   throw new AppException(ErrorCode.ASSET_TYPE_EXISTED);
               });
        // Kiểm tra trùng tên + nhóm (nếu cần)
        assetTypeRepository.findByNameAssetTypeAndAssetGroupAndIdNot(
                        request.getNameAssetType(), request.getAssetGroup(), assetTypeId)
                .ifPresent(existing -> {
                    throw new AppException(ErrorCode.ASSET_TYPE_EXISTED);
                });

       assetTypeMapper.updateAssetType(request, assetType);
       assetType.setUpdatedAt(Instant.now());
       return assetTypeMapper.toResponse(assetTypeRepository.save(assetType));
    }

    @Override
    public void deleteAssetTypeById(String assetTypeId) {
        if (!assetTypeRepository.existsById(assetTypeId)) {
            throw new AppException(ErrorCode.ASSSET_TYPE_NOT_FOUND);
        }
        assetTypeRepository.deleteById(assetTypeId);
    }


}
