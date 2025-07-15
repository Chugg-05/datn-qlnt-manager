package com.example.datn_qlnt_manager.service.implement;


import com.example.datn_qlnt_manager.common.JobStatus;
import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.JobFilter;
import com.example.datn_qlnt_manager.dto.request.job.JobCreationRequest;
import com.example.datn_qlnt_manager.dto.request.job.JobUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.job.JobResponse;
import com.example.datn_qlnt_manager.entity.Building;
import com.example.datn_qlnt_manager.entity.Job;
import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.JobMapper;
import com.example.datn_qlnt_manager.repository.BuildingRepository;
import com.example.datn_qlnt_manager.repository.JobRepository;
import com.example.datn_qlnt_manager.service.JobService;
import com.example.datn_qlnt_manager.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobServiceImpl implements JobService {
    JobRepository jobRepository;
    JobMapper jobMapper;
    BuildingRepository buildingRepository;
    UserService userService;
    CodeGeneratorService codeGeneratorService;

    @Override
    public PaginatedResponse<JobResponse> getPageAndSearchAndFilterJobByUserId(
            JobFilter filter,
            int page,
            int size
    ){
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        var user = userService.getCurrentUser();

        Page<Job> paging = jobRepository.getPageAndSearchAndFilterJobByUserId(
                user.getId(),
                filter.getBuildingId(),
                filter.getQuery(),
                filter.getJobPriorityLevel(),
                filter.getJobStatus(),
                filter.getJobObjectType(),
                pageable
        );

        return buildPaginatedJobResponse(paging, page, size);
    }

    @Override
    public List<JobResponse> getAllJobByUserId(){
        User user = userService.getCurrentUser();
        List<Job> jobs = jobRepository.findAllByUserId(user.getId());
        return jobs.stream().map(jobMapper::toJobResponse).toList();
    }

    @Override
    public JobResponse createJob(JobCreationRequest request){

        Building building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new AppException(ErrorCode.BUILDING_NOT_FOUND));

        String jobCode = codeGeneratorService.generateJobCode(building);

        Job job = jobMapper.toJob(request);

        job.setBuilding(building);
        job.setJobCode(jobCode);
        job.setJobStatus(JobStatus.MOI);
        job.setCreatedAt(Instant.now());
        job.setUpdatedAt(Instant.now());

        return jobMapper.toJobResponse(jobRepository.save(job));
    }

    @Override
    public JobResponse updateJob(String jobId, JobUpdateRequest request){
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        Date oldDeadline = job.getCompletionDeadline();
        Date newDeadline = request.getCompletionDeadline();
        if (!oldDeadline.equals(newDeadline)) {
            if (newDeadline.before(new Date())) {
                throw new AppException(ErrorCode.INVALID_COMPLETION_DEADLINE_PAST);
            }
        }

        jobMapper.updateJob(job, request);
        job.setUpdatedAt(Instant.now());
        return jobMapper.toJobResponse(jobRepository.save(job));
    }

    @Override
    public void softDeleteJobById(String jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        job.setJobStatus(JobStatus.DA_HUY);
        jobMapper.toJobResponse(jobRepository.save(job));
    }

    @Override
    public void deleteJobById(String jobId) {
        if (!jobRepository.existsById(jobId)) {
            throw new AppException(ErrorCode.JOB_NOT_FOUND);
        }
        jobRepository.deleteById(jobId);
    }

    @Override
    public void completeWord(String jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        job.setJobStatus(JobStatus.DA_XONG);
        jobMapper.toJobResponse(jobRepository.save(job));
    }

    private PaginatedResponse<JobResponse> buildPaginatedJobResponse(
            Page<Job> paging, int page, int size
    ){
        List<JobResponse> jobs = paging.getContent().stream()
                .map(jobMapper::toJobResponse)
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

        return PaginatedResponse.<JobResponse>builder()
                .data(jobs)
                .meta(meta)
                .build();
    }

}
