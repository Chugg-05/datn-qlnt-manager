package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.entity.FeedbackProcessHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackProcessHistoryRepository extends CrudRepository<FeedbackProcessHistory, String> {
}
