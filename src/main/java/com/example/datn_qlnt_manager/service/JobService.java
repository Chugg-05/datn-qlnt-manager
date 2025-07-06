package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.JobFilter;
import com.example.datn_qlnt_manager.dto.request.job.JobCreationRequest;
import com.example.datn_qlnt_manager.dto.request.job.JobUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.job.JobResponse;

import java.util.List;

public interface JobService {
    PaginatedResponse<JobResponse> getPageAndSearchAndFilterJobByUserId(
            JobFilter filter,
            int page,
            int size
    );

    List<JobResponse> getAllJobByUserId();

    JobResponse createJob (JobCreationRequest request);

    JobResponse updateJob (String jobId, JobUpdateRequest request);

    void softDeleteJobById(String jobId);

    void deleteJobById(String jobId);

    void completeWord(String jobId);
}
