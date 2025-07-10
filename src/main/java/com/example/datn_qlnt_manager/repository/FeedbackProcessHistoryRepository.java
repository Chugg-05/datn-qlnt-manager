package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.dto.response.feedbackProcessHistory.FeedbackProcessHistoryResponse;
import com.example.datn_qlnt_manager.entity.FeedbackProcessHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackProcessHistoryRepository extends CrudRepository<FeedbackProcessHistory, String> {
    @Query("""
        SELECT new com.example.datn_qlnt_manager.dto.response.feedbackProcessHistory.FeedbackProcessHistoryResponse(
            h.id,
            f.id,
            f.content,
            u.id,
            u.fullName,
            h.note,
            h.time
        )
        FROM FeedbackProcessHistory h
        JOIN h.feedback f
        JOIN h.user u
        WHERE u.id = :userId
          AND (:feedbackId IS NULL OR f.id = :feedbackId)
          AND (
              :query IS NULL OR :query = '' OR
              LOWER(h.note) LIKE LOWER(CONCAT('%', :query, '%')) OR
              LOWER(f.content) LIKE LOWER(CONCAT('%', :query, '%')) OR
              LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%'))
          )
        ORDER BY h.time DESC
    """)
    Page<FeedbackProcessHistoryResponse> findAllByCurrentUser(
            @Param("userId") String userId,
            @Param("feedbackId") String feedbackId,
            @Param("query") String query,
            Pageable pageable
    );
}
