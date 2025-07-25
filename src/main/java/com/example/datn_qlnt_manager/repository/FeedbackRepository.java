package com.example.datn_qlnt_manager.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.common.FeedbackStatus;
import com.example.datn_qlnt_manager.common.FeedbackType;
import com.example.datn_qlnt_manager.dto.response.feedback.FeedbackResponse;
import com.example.datn_qlnt_manager.dto.response.feedback.FeedbackSelfResponse;
import com.example.datn_qlnt_manager.entity.Feedback;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, String> {

    List<Feedback> findAllByTenantIdAndRoomIdAndFeedbackTypeAndFeedbackStatusIn(
            String tenantId, String roomId, FeedbackType feedbackType, List<FeedbackStatus> statuses);

    // khách thuê xem lại feedback
    @Query(
            """
	SELECT new com.example.datn_qlnt_manager.dto.response.feedback.FeedbackSelfResponse(
		t.fullName,
		r.roomCode,
		f.content,
		f.rating,
		f.feedbackStatus,
		f.feedbackType,
		f.attachment,
		f.createdAt,
		f.updatedAt
	)
	FROM Feedback f
	JOIN f.tenant t
	JOIN f.room r
	WHERE t.owner.id = :ownerId
	AND (:rating IS NULL OR f.rating = :rating)
	AND (:feedbackType IS NULL OR f.feedbackType = :feedbackType)
	AND (:feedbackStatus IS NULL OR f.feedbackStatus = :feedbackStatus)
	AND (:query IS NULL OR LOWER(f.content) LIKE LOWER(CONCAT('%', :query, '%')))
	ORDER BY f.updatedAt DESC
""")
    Page<FeedbackSelfResponse> findByTenantOwnerIdWithFilter(
            @Param("ownerId") String ownerId,
            @Param("rating") Integer rating,
            @Param("feedbackType") FeedbackType feedbackType,
            @Param("feedbackStatus") FeedbackStatus feedbackStatus,
            @Param("query") String query,
            Pageable pageable);

    // quản lí xem feedback của khách thuê theo tòa của họ
    @Query(
            """
	SELECT new com.example.datn_qlnt_manager.dto.response.feedback.FeedbackResponse(
		f.id,
		t.id,
		t.fullName,
		r.id,
		r.roomCode,
		f.content,
		f.feedbackType,
		f.rating,
		f.attachment,
		f.feedbackStatus,
		f.createdAt,
		f.updatedAt
	)
	FROM Feedback f
	JOIN f.tenant t
	JOIN f.room r
	JOIN r.floor fl
	JOIN fl.building b
	WHERE b.user.id = :userId
	AND (:buildingId IS NULL OR b.id = :buildingId)
	AND (:status IS NULL OR f.feedbackStatus =  :status)
	AND (:rating IS NULL OR f.rating = :rating)
	AND (:feedbackType IS NULL OR f.feedbackType = :feedbackType)
	AND (
		:query IS NULL OR :query = '' OR
		LOWER(f.content) LIKE LOWER(CONCAT('%', :query, '%')) OR
		LOWER(t.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR
		LOWER(r.roomCode) LIKE LOWER(CONCAT('%', :query, '%'))
	)
	ORDER BY f.updatedAt DESC
""")
    Page<FeedbackResponse> findAllByFilter(
            @Param("userId") String userId,
            @Param("buildingId") String buildingId,
            @Param("status") FeedbackStatus status,
            @Param("rating") Integer rating,
            @Param("feedbackType") FeedbackType feedbackType,
            @Param("query") String query,
            Pageable pageable);
}
