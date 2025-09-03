package com.example.datn_qlnt_manager.service.implement;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.example.datn_qlnt_manager.common.*;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.FeedBackSelfFilter;
import com.example.datn_qlnt_manager.dto.filter.FeedbackFilter;
import com.example.datn_qlnt_manager.dto.request.feedback.*;
import com.example.datn_qlnt_manager.dto.request.tenant.TenantNewCreationRequest;
import com.example.datn_qlnt_manager.repository.*;
import com.example.datn_qlnt_manager.utils.CloudinaryUtil;
import com.example.datn_qlnt_manager.utils.FormatUtil;
import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.dto.response.feedback.FeedbackResponse;
import com.example.datn_qlnt_manager.entity.*;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.FeedbackMapper;
import com.example.datn_qlnt_manager.service.FeedbackService;
import com.example.datn_qlnt_manager.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedbackServiceImpl implements FeedbackService {

    FeedbackRepository feedbackRepository;
    TenantRepository tenantRepository;
    ContractRepository contractRepository;
    RoomRepository roomRepository;
    VehicleRepository vehicleRepository;
    FeedbackMapper feedbackMapper;
    UserService userService;
    FeedbackProcessHistoryRepository feedbackProcessHistoryRepository;
    CloudinaryUtil cloudinaryUtil;
    Cloudinary cloudinary;

    @Override
    @Transactional
    public FeedbackResponse createFeedback(FeedbackCreationRequest request) {

        Tenant tenant = tenantRepository.findByUserId(currentUser().getId())
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        Room room = validateCreateFeedback(tenant, request.getRoomId());

        String normalizedContent = normalizeContent(request.getContent());
        List<Feedback> existingFeedbacks =
                feedbackRepository.findAllByNameSenderAndRoomCodeAndFeedbackTypeAndFeedbackStatusIn(
                        currentUser().getFullName(),
                        room.getRoomCode(),
                        request.getFeedbackType(),
                        List.of(FeedbackStatus.CHO_XU_LY)
                );

        boolean isDuplicate = existingFeedbacks.stream()
                .anyMatch(f -> f.getContent() != null &&
                        normalizeContent(f.getContent()).equals(normalizedContent));

        if (isDuplicate) {
            throw new AppException(ErrorCode.FEED_BACK_DUPLICATED);
        }

        Feedback feedback = feedbackMapper.toEntity(request);
        feedback.setNameSender(currentUser().getFullName());
        feedback.setRoomCode(room.getRoomCode());
        feedback.setCreatedAt(Instant.now());
        feedback.setUpdatedAt(Instant.now());

        Feedback savedFeedback = feedbackRepository.save(feedback);

        FeedbackProcessHistory history = FeedbackProcessHistory.builder()
                .feedback(savedFeedback)
                .user(currentUser())
                .note("Khách thuê gửi phản hồi")
                .time(savedFeedback.getUpdatedAt())
                .build();
        feedbackProcessHistoryRepository.save(history);

        return feedbackMapper.toResponse(savedFeedback);
    }

    @Transactional
    @Override
    public FeedbackResponse createFeedbackVehicleByTenant(FeedbackCreationVehicleRequest request, MultipartFile vehicleRegistrationCard) {
        Tenant tenant = tenantRepository.findByUserId(currentUser().getId())
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        Room room = validateCreateFeedback(tenant, request.getRoomId());

        String content = "Khách thuê: " + tenant.getFullName() + ", ở phòng: "
                + room.getRoomCode() + ". Yêu cầu muốn thêm phương tiện, với lý do: "
                + request.getReason();

        String normalizedContent = normalizeContent(content);
        List<Feedback> existingFeedbacks =
                feedbackRepository.findAllByNameSenderAndRoomCodeAndFeedbackTypeAndFeedbackStatusIn(
                        currentUser().getFullName(),
                        room.getRoomCode(),
                        FeedbackType.HO_TRO,
                        List.of(FeedbackStatus.CHO_XU_LY)
                );

        boolean isDuplicate = existingFeedbacks.stream()
                .anyMatch(f -> f.getContent() != null &&
                        normalizeContent(f.getContent()).equals(normalizedContent));

        if (isDuplicate) {
            throw new AppException(ErrorCode.FEED_BACK_DUPLICATED);
        }

        if (vehicleRegistrationCard == null || vehicleRegistrationCard.isEmpty()) {
            throw new AppException(ErrorCode.IMAGE_FEEDBACK_REQUIRED);
        }

        checkDuplicateFeedback(room, content);

        Feedback feedback = new Feedback();
        feedback.setNameSender(currentUser().getFullName());
        feedback.setRoomCode(room.getRoomCode());
        feedback.setFeedbackType(FeedbackType.HO_TRO);
        feedback.setFeedbackName("Yêu cầu thêm phương tiện mới cho khách thuê phòng: " + room.getRoomCode() + ", tòa: " + room.getFloor().getBuilding().getBuildingName());
        feedback.setContent(content);
        feedback.setFeedbackStatus(FeedbackStatus.CHO_XU_LY);
        feedback.setCreatedAt(Instant.now());
        feedback.setUpdatedAt(Instant.now());
        feedback.setAttachment(cloudinaryUtil.uploadImage(vehicleRegistrationCard,"feedback"));


        Feedback savedFeedback = feedbackRepository.save(feedback);

        FeedbackProcessHistory history = FeedbackProcessHistory.builder()
                .feedback(savedFeedback)
                .user(currentUser())
                .note("Khách thuê gửi yêu cầu thêm phương tiện vào phòng")
                .time(savedFeedback.getUpdatedAt())
                .build();
        feedbackProcessHistoryRepository.save(history);

        return feedbackMapper.toResponse(savedFeedback);
    }


    @Transactional
    @Override
    public FeedbackResponse createFeedbackDeleteTenant(FeedbackDeleteTenantRequest request) {

        Tenant tenant = tenantRepository.findByUserId(currentUser().getId())
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        Room room = validateCreateFeedback(tenant, request.getRoomId());

        List<Tenant> tenants = tenantRepository.findAllTenantsByRoomId(request.getRoomId());
        List<Tenant> tenantStopRents = tenants.stream()
                .filter(t-> request.getTenantId().contains(t.getId()))
                .peek(t -> {
                    if (t.getHasAccount()) {
                        throw new AppException(ErrorCode.CANNOT_DELETE_REPRESENTATIVE);
                    }
                })
                .toList();

        String content = "Xin thông báo, thành viên" + ":\\n\\n"
                + tenantStopRents.stream()
                .map(t->"- " + t.getFullName() + " - ngày sinh: " + t.getDob())
                .collect(Collectors.joining("\n"))
                + "\n\nThuộc phòng: " + room.getRoomCode() + ", tòa: " + room.getFloor().getBuilding().getBuildingName()
                + ", sẽ không tiếp tục thuê phòng từ ngày"
                + request.getEndDate() +", mong quản lý cập nhật danh sách thành viên và điều chỉnh hợp đồng(nếu cần)";

        checkDuplicateFeedback(room, content);

        Feedback feedback = new Feedback();
        feedback.setNameSender(currentUser().getFullName());
        feedback.setRoomCode(room.getRoomCode());
        feedback.setFeedbackType(FeedbackType.HO_TRO);
        feedback.setFeedbackName("Thông báo có thành viên rời phòng: " + room.getRoomCode() + ", tòa: " + room.getFloor().getBuilding().getBuildingName());
        feedback.setContent(content);
        feedback.setFeedbackStatus(FeedbackStatus.CHO_XU_LY);
        feedback.setCreatedAt(Instant.now());
        feedback.setUpdatedAt(Instant.now());

        Feedback savedFeedback = feedbackRepository.save(feedback);

        FeedbackProcessHistory history = FeedbackProcessHistory.builder()
                .feedback(savedFeedback)
                .user(currentUser())
                .note("Khách thuê gửi yêu cầu cập nhật thành viên phòng")
                .time(savedFeedback.getUpdatedAt())
                .build();
        feedbackProcessHistoryRepository.save(history);

        return feedbackMapper.toResponse(savedFeedback);
    }

    @Transactional
    @Override
    public FeedbackResponse changeRepreserntative(FeedbackChangeRepresentativeRequest request, List<MultipartFile> CCCD) {

        if (CCCD == null || CCCD.isEmpty()) {
            throw new AppException(ErrorCode.IMAGE_FEEDBACK_REQUIRED);
        }

        Tenant tenant = tenantRepository.findByUserId(currentUser().getId())
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        Room room = validateCreateFeedback(tenant, request.getRoomId());

        List<Contract> contracts = contractRepository.findByRoomIdAndStatusIn(
                request.getRoomId(),
                List.of(ContractStatus.HIEU_LUC, ContractStatus.SAP_HET_HAN)
        );

        if (contracts.isEmpty()) {
            throw new AppException(ErrorCode.CONTRACT_NOT_FOUND);
        }

        String content;
        String attachment = null;

        // --- Xử lý theo loại thay đổi ---
        if (request.getChangeType() == RepresentativeChangeType.NGUOI_CUNG_PHONG) {

            if (request.getTenantId() == null || request.getTenantId().isBlank()) {
                throw new AppException(ErrorCode.INVALID_TENANT_ID_BLANK);
            }

            Tenant newRepresentative = tenantRepository.findById(request.getTenantId())
                    .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

            if (tenant.getId().equals(newRepresentative.getId())) {
                throw new AppException(ErrorCode.TENANT_REPRESENTATIVE_DUPLICATE);
            }

            content = "Khách thuê: " + tenant.getFullName() + " (CMND/CCCD: " + tenant.getIdentityCardNumber() + ")"
                    + ", đã gửi yêu cầu thay đổi người đại diện cho khách thuê cùng phòng: " + newRepresentative.getFullName()
                    + " (CMND/CCCD: " + newRepresentative.getIdentityCardNumber() + "). "
                    + "Phòng: " + room.getRoomCode()
                    + ", tòa: " + room.getFloor().getBuilding().getBuildingName()
                    + ". Lý do: " + request.getReason();



        } else if (request.getChangeType() == RepresentativeChangeType.NGUOI_NGOAI) {
            if (request.getNewTenant() == null) {
                throw new AppException(ErrorCode.NEW_TENANT_REQUIRED);
            }
            if (request.getStayInRoom() == null) {
                throw new AppException(ErrorCode.STAY_IN_ROOM_REQUIRED);
            }

            TenantNewCreationRequest newTenant = request.getNewTenant();

            // Upload ảnh từ danh sách CCCD (front + back)
            String frontUrl;
            String backUrl;

            frontUrl = uploadFile(CCCD.getFirst());
            backUrl = uploadFile(CCCD.get(1));

            // Ghép lại chuỗi attachment
            attachment = Stream.of(frontUrl, backUrl)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(","));


            content = "Khách thuê: " + tenant.getFullName() + " (CMND/CCCD: " + tenant.getIdentityCardNumber() + ")"
                    + ", đã gửi yêu cầu nhượng quyền đại diện cho người ngoài: " + newTenant.getFullName()
                    + " (CMND/CCCD: " + newTenant.getIdentityCardNumber() + "). "
                    + "Phòng: " + room.getRoomCode()
                    + ", tòa: " + room.getFloor().getBuilding().getBuildingName()
                    + ". Người gửi " + (request.getStayInRoom() ? "vẫn ở lại phòng." : "sẽ rời khỏi phòng.")
                    + " Lý do: " + request.getReason();
        } else {
            throw new AppException(ErrorCode.INVALID_REPRESENTATIVE_CHANGE_TYPE_NULL);
        }

        checkDuplicateFeedback(room, content);

        Feedback feedback = new Feedback();
        feedback.setNameSender(currentUser().getFullName());
        feedback.setRoomCode(room.getRoomCode());
        feedback.setFeedbackType(FeedbackType.HO_TRO);
        feedback.setFeedbackName("Yêu cầu thay đổi đại diện phòng: " + room.getRoomCode() + ", tòa: " + room.getFloor().getBuilding().getBuildingName());
        feedback.setContent(content);
        feedback.setAttachment(attachment);
        feedback.setFeedbackStatus(FeedbackStatus.CHO_XU_LY);
        feedback.setCreatedAt(Instant.now());
        feedback.setUpdatedAt(Instant.now());

        Feedback savedFeedback = feedbackRepository.save(feedback);

        FeedbackProcessHistory history = FeedbackProcessHistory.builder()
                .feedback(savedFeedback)
                .user(currentUser())
                .note("Khách thuê gửi yêu cầu thay đổi đại diện phòng")
                .time(savedFeedback.getUpdatedAt())
                .build();
        feedbackProcessHistoryRepository.save(history);

        return feedbackMapper.toResponse(savedFeedback);

    }


    @Override
    public PaginatedResponse<FeedbackResponse> filterMyFeedbacks(FeedBackSelfFilter filter, int page, int size) {
        String currentUserName = userService.getCurrentUser().getFullName();

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        Page<Feedback> pageResult = feedbackRepository.findAllBySenderWithFilter(
                currentUserName,
                filter.getRating(),
                filter.getFeedbackType(),
                filter.getFeedbackStatus(),
                filter.getQuery(),
                pageable
        );

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(pageResult.getNumberOfElements())
                        .perPage(size)
                        .currentPage(page)
                        .totalPages(pageResult.getTotalPages())
                        .total(pageResult.getTotalElements())
                        .build())
                .build();

        List<FeedbackResponse> responses = pageResult.stream()
                .map(feedbackMapper::toResponse)
                .toList();

        return PaginatedResponse.<FeedbackResponse>builder()
                .data(responses)
                .meta(meta)
                .build();
    }

    @Override
    public PaginatedResponse<FeedbackResponse> filterFeedbacksForManager(FeedbackFilter filter, int page, int size) {
        String userId = userService.getCurrentUser().getId();

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<FeedbackResponse> pageResult = feedbackRepository.findAllByFilter(
                userId,
                filter.getBuildingId(),
                filter.getFeedbackStatus(),
                filter.getRating(),
                filter.getFeedbackType(),
                filter.getQuery(),
                pageable);

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(pageResult.getNumberOfElements())
                        .currentPage(page)
                        .perPage(size)
                        .total(pageResult.getTotalElements())
                        .totalPages(pageResult.getTotalPages())
                        .build())
                .build();

        return PaginatedResponse.<FeedbackResponse>builder()
                .data(pageResult.getContent())
                .meta(meta)
                .build();
    }

    @Override
    @Transactional
    public FeedbackResponse rejectFeedback(String feedbackId, RejectFeedbackRequest request) {
        User currentUser = userService.getCurrentUser();

        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new AppException(ErrorCode.FEED_BACK_NOT_FOUND));

        if (feedback.getFeedbackStatus() != FeedbackStatus.CHO_XU_LY) {
            throw new AppException(ErrorCode.INVALID_FEEDBACK_STATUS);
        }

        checkOwnerPermission(feedback, currentUser.getId());

        feedback.setFeedbackStatus(FeedbackStatus.TU_CHOI);
        feedback.setRejectionReason(request.getRejectionReason());
        feedback.setUpdatedAt(Instant.now());
        Feedback savedFeedback = feedbackRepository.save(feedback);

        FeedbackProcessHistory history = getFeedbackHistory(savedFeedback);
        history.setNote("Chủ building từ chối feedback");
        history.setTime(savedFeedback.getUpdatedAt());
        feedbackProcessHistoryRepository.save(history);

        return feedbackMapper.toResponse(savedFeedback);
    }

    @Override
    @Transactional
    public FeedbackResponse startProcessing(String feedbackId) {
        User currentUser = userService.getCurrentUser();
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new AppException(ErrorCode.FEED_BACK_NOT_FOUND));

        checkOwnerPermission(feedback, currentUser.getId());

        if (feedback.getFeedbackStatus() != FeedbackStatus.CHO_XU_LY) {
            throw new AppException(ErrorCode.INVALID_FEEDBACK_STATUS);
        }

        feedback.setFeedbackStatus(FeedbackStatus.DANG_XU_LY);
        feedback.setUpdatedAt(Instant.now());
        Feedback savedFeedback = feedbackRepository.save(feedback);

        FeedbackProcessHistory history = getFeedbackHistory(savedFeedback);
        history.setNote("Chủ building bắt đầu xử lý feedback");
        history.setTime(savedFeedback.getUpdatedAt());
        feedbackProcessHistoryRepository.save(history);

        return feedbackMapper.toResponse(savedFeedback);
    }

    @Override
    @Transactional
    public FeedbackResponse completeProcessing(String feedbackId) {
        User currentUser = userService.getCurrentUser();
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new AppException(ErrorCode.FEED_BACK_NOT_FOUND));

        checkOwnerPermission(feedback, currentUser.getId());

        if (feedback.getFeedbackStatus() != FeedbackStatus.DANG_XU_LY) {
            throw new AppException(ErrorCode.INVALID_FEEDBACK_STATUS_COMPLETE);
        }

        feedback.setFeedbackStatus(FeedbackStatus.DA_XU_LY);
        feedback.setUpdatedAt(Instant.now());
        Feedback savedFeedback = feedbackRepository.save(feedback);

        FeedbackProcessHistory history = getFeedbackHistory(savedFeedback);
        history.setNote("Chủ building hoàn tất xử lý feedback");
        history.setTime(savedFeedback.getUpdatedAt());
        feedbackProcessHistoryRepository.save(history);

        return feedbackMapper.toResponse(savedFeedback);
    }

    @Override
    public FeedbackResponse changeVehicleFeedBack(FeedbackChangeVehicleRequest request, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new AppException(ErrorCode.IMAGE_FEEDBACK_REQUIRED);
        }

        Tenant tenant = tenantRepository.findByUserId(currentUser().getId())
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        Room room = validateCreateFeedback(tenant, request.getRoomId());

        Vehicle vehicle = vehicleRepository.findById(request.getOldVehicleId())
                .orElseThrow(() -> new AppException(ErrorCode.VEHICLE_NOT_FOUND));

        String content = "Khách hàng " + tenant.getFullName() + " ở phòng " + room.getRoomCode()
                + " yêu cầu thay đổi phương tiện "
                + FormatUtil.formatVehicleType(vehicle.getVehicleType()) + " - " + vehicle.getLicensePlate()
                + " với lý do là: " + request.getReason();

        checkDuplicateFeedback(room, content);

        Feedback feedback = new Feedback();
        feedback.setNameSender(currentUser().getFullName());
        feedback.setRoomCode(room.getRoomCode());
        feedback.setFeedbackType(FeedbackType.HO_TRO);
        feedback.setFeedbackName("Yêu cầu thay đổi phương tiện cho khách thuê phòng: "
                + room.getRoomCode() + ", tòa: " + room.getFloor().getBuilding().getBuildingName());
        feedback.setContent(content);
        feedback.setFeedbackStatus(FeedbackStatus.CHO_XU_LY);
        feedback.setAttachment(cloudinaryUtil.uploadImage(image, "feedback"));
        feedback.setCreatedAt(Instant.now());
        feedback.setUpdatedAt(Instant.now());

        Feedback savedFeedback = feedbackRepository.save(feedback);


        FeedbackProcessHistory history = FeedbackProcessHistory.builder()
                .feedback(savedFeedback)
                .user(currentUser())
                .note("Khách thuê gửi yêu cầu thay đổi phương tiện vào phòng")
                .time(savedFeedback.getUpdatedAt())
                .build();
        feedbackProcessHistoryRepository.save(history);

        return feedbackMapper.toResponse(savedFeedback);
    }

    @Override
    public FeedbackResponse FeedbackTerminateContract(FeedbackTerminateContractRequest request) {
        Tenant tenant = tenantRepository.findByUserId(currentUser().getId())
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        Room room = validateCreateFeedback(tenant, request.getRoomId());

        Contract contract = contractRepository.findByTenantIdAndStatusIn(
                        tenant.getId(),
                        List.of(ContractStatus.HIEU_LUC, ContractStatus.SAP_HET_HAN))
                .stream()
                .filter(c -> c.getRoom().getId().equals(request.getRoomId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_RENT_ROOM));

        if (request.getContractFeedbackType() == ContractFeedbackType.TERMINATE) {
            LocalDate terminatedDate = request.getTerminateDate();
            if (terminatedDate == null) {
                throw new AppException(ErrorCode.TERMINATE_DATE_NOT_BLANK);
            }
            if (terminatedDate.isAfter(contract.getEndDate())) {
                throw new AppException(ErrorCode.INVALID_TERMINATE_DATE);
            }
        } else if (request.getContractFeedbackType() == ContractFeedbackType.EXTEND) {
            LocalDate extendDate = request.getExtendDate();
            if (extendDate == null) {
                throw new AppException(ErrorCode.INVALID_EXTEND_DATE_BLANK);
            }
            if (!extendDate.isAfter(contract.getEndDate())) {
                throw new AppException(ErrorCode.INVALID_EXTEND_DATE_AFTER);
            }
        }

        String content = buildFeedbackContent(
                request.getContractFeedbackType(), tenant, room,
                request.getTerminateDate(), request.getExtendDate()
        );

        String feedbackName = buildFeedbackName(
                request.getContractFeedbackType(), room,
                request.getTerminateDate(), request.getExtendDate()
        );

        checkDuplicateFeedback(room, content);

        Feedback feedback = new Feedback();
        feedback.setNameSender(currentUser().getFullName());
        feedback.setRoomCode(room.getRoomCode());
        feedback.setFeedbackType(FeedbackType.HO_TRO);
        feedback.setFeedbackName(feedbackName);
        feedback.setContent(content);
        feedback.setFeedbackStatus(FeedbackStatus.CHO_XU_LY);
        feedback.setCreatedAt(Instant.now());
        feedback.setUpdatedAt(Instant.now());

        Feedback savedFeedback = feedbackRepository.save(feedback);

        FeedbackProcessHistory history = FeedbackProcessHistory.builder()
                .feedback(savedFeedback)
                .user(currentUser())
                .note(buildFeedbackNote(request.getContractFeedbackType()))
                .time(savedFeedback.getUpdatedAt())
                .build();
        feedbackProcessHistoryRepository.save(history);

        return feedbackMapper.toResponse(savedFeedback);
    }

    @Override
    public FeedbackResponse rateFeedback(String feedbackId, FeedbackRatingRequest request) {
        User currentUser = userService.getCurrentUser();

        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new AppException(ErrorCode.FEED_BACK_NOT_FOUND));

        if (!feedback.getNameSender().equals(currentUser.getFullName())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (feedback.getFeedbackStatus() != FeedbackStatus.DA_XU_LY) {
            throw new AppException(ErrorCode.FEEDBACK_NOT_COMPLETED);
        }

        if (feedback.getRating() != null) {
            throw new AppException(ErrorCode.FEEDBACK_RATING_ALREADY_RATED);
        }

        feedback.setRating(request.getRating());
        feedback.setUpdatedAt(Instant.now());

        Feedback savedFeedback = feedbackRepository.save(feedback);

        FeedbackProcessHistory history = FeedbackProcessHistory.builder()
                .feedback(savedFeedback)
                .user(currentUser)
                .note("Khách thuê đã đánh giá phản hồi với số sao: " + request.getRating())
                .time(savedFeedback.getUpdatedAt())
                .build();
        feedbackProcessHistoryRepository.save(history);

        return feedbackMapper.toResponse(savedFeedback);
    }

    String normalizeContent(String content) {
        if (content == null) return "";
        return content.trim()
                .toLowerCase()
                .replaceAll("\\p{Punct}", "")
                .replaceAll("\\s+", " ");
    }

    private void checkOwnerPermission(Feedback feedback, String userId) {
        List<Room> rooms = roomRepository.findAllByRoomCode(feedback.getRoomCode());
        if (rooms.isEmpty()) {
            throw new AppException(ErrorCode.ROOM_NOT_FOUND);
        }

        boolean isOwner = rooms.stream()
                .anyMatch(r -> r.getFloor().getBuilding().getUser().getId().equals(userId));

        if (!isOwner) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    private FeedbackProcessHistory getFeedbackHistory(Feedback feedback) {
        return feedbackProcessHistoryRepository.findAllByFeedbackId(feedback.getId())
                .orElseThrow(() -> new AppException(ErrorCode.FEED_BACK_HISTORY_NOT_FOUND));
    }

    private User currentUser() {
        return userService.getCurrentUser();
    }



    public String uploadFile(MultipartFile file) {
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Upload file thất bại", e);
        }
    }

    private Room validateCreateFeedback (Tenant tenant, String roomId) {
        List<Contract> contracts = contractRepository.findByTenantIdAndStatusIn(
                tenant.getId(),
                List.of(ContractStatus.HIEU_LUC, ContractStatus.SAP_HET_HAN)
        );

        if (contracts.isEmpty()) {
            throw new AppException(ErrorCode.TENANT_NOT_IN_CONTRACT);
        }

        Contract  contract = contracts.stream()
                .filter(c -> c.getRoom().getId().equals(roomId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_RENT_ROOM));

        Room room = contract.getRoom();
        if (room == null) {
            throw new AppException(ErrorCode.ROOM_NOT_FOUND);
        }

        return room;
    }

    private void checkDuplicateFeedback(Room room, String content) {
        String normalizedContent = normalizeContent(content);

        List<Feedback> existingFeedbacks =
                feedbackRepository.findAllByNameSenderAndRoomCodeAndFeedbackTypeAndFeedbackStatusIn(
                        currentUser().getFullName(),
                        room.getRoomCode(),
                        FeedbackType.HO_TRO,
                        List.of(FeedbackStatus.CHO_XU_LY)
                );

        boolean isDuplicate = existingFeedbacks.stream()
                .filter(f -> f.getContent() != null)
                .map(f -> normalizeContent(f.getContent()))
                .anyMatch(c -> c.equals(normalizedContent));

        if (isDuplicate) {
            throw new AppException(ErrorCode.FEED_BACK_DUPLICATED);
        }
    }


    private String buildFeedbackContent(ContractFeedbackType type, Tenant tenant, Room room, LocalDate terminateDate, LocalDate extendDate) {
        String buildingName = room.getFloor().getBuilding().getBuildingName();
        String roomCode = room.getRoomCode();

        return switch (type) {
            case TERMINATE -> "Khách hàng " + tenant.getFullName() + " yêu cầu hỗ trợ chấm dứt hợp đồng phòng " + roomCode
                    + " tại tòa " + buildingName
                    + " trước thời hạn, vào ngày " + terminateDate;
            case EXTEND -> "Khách hàng " + tenant.getFullName() + " yêu cầu hỗ trợ gia hạn hợp đồng phòng " + roomCode
                    + " tại tòa " + buildingName
                    + " đến ngày " + extendDate;
        };
    }

    private String buildFeedbackName(ContractFeedbackType type, Room room, LocalDate terminateDate, LocalDate extendDate) {
        String buildingName = room.getFloor().getBuilding().getBuildingName();
        String roomCode = room.getRoomCode();

        return switch (type) {
            case TERMINATE -> "Yêu cầu hỗ trợ chấm dứt hợp đồng trước hạn - phòng: "
                    + roomCode + ", tòa: " + buildingName
                    + " vào ngày: " + terminateDate;
            case EXTEND -> "Yêu cầu hỗ trợ gia hạn hợp đồng - phòng: "
                    + roomCode + ", tòa: " + buildingName
                    + " đến ngày: " + extendDate;
        };
    }

    private String buildFeedbackNote(ContractFeedbackType type) {
        return switch (type) {
            case TERMINATE -> "Khách thuê gửi yêu cầu chấm dứt hợp đồng trước thời hạn";
            case EXTEND -> "Khách thuê gửi yêu cầu gia hạn hợp đồng";
        };
    }
}
