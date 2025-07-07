package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.JobFilter;
import com.example.datn_qlnt_manager.dto.request.job.JobCreationRequest;
import com.example.datn_qlnt_manager.dto.request.job.JobUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.job.JobResponse;
import com.example.datn_qlnt_manager.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/jobs")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Job", description = "API Job")
public class JobController {

    JobService jobService;


    @Operation(summary = "Phân trang, tìm kiếm, lọc công việc")
    @GetMapping
    public ApiResponse<List<JobResponse>> getPageAndSearchAndFilterJob(
            @ModelAttribute JobFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size
            ) {
        PaginatedResponse<JobResponse> result = jobService.getPageAndSearchAndFilterJobByUserId(
                filter,
                page,
                size
        );

        return ApiResponse.<List<JobResponse>>builder()
                .message("Get Job successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Lấy danh sách công việc theo user ID")
    @GetMapping("/all")
    public ApiResponse<List<JobResponse>> getAllJobs (){
        List<JobResponse> jobs = jobService.getAllJobByUserId();
        return ApiResponse.<List<JobResponse>>builder()
                .message("Get all jobs by user ID successfully")
                .data(jobs)
                .build();
    }

    @Operation(summary = "Thêm công việc")
    @PostMapping
    public ApiResponse<JobResponse> createJob(@Valid @RequestBody JobCreationRequest request){
        return ApiResponse.<JobResponse>builder()
                .message("Job has been created!")
                .data(jobService.createJob(request))
                .build();
    }

    @Operation(summary = "Cập nhật công việc")
    @PutMapping("/{jobId}")
    public ApiResponse<JobResponse> updateJob (
            @Valid @RequestBody JobUpdateRequest request, @PathVariable("jobId") String jobId
            ){
        return ApiResponse.<JobResponse>builder()
                .message("Job updated!")
                .data(jobService.updateJob(jobId, request))
                .build();
    }

    @Operation(summary = "Hoàn thành công việc")
    @PutMapping("/complete-word/{jobId}")
    public ApiResponse<String> completeWord (@PathVariable("jobId") String jobId) {
        jobService.completeWord(jobId);
        return ApiResponse.<String>builder().message("The work has been completed!").build();
    }

    @Operation(summary = "Xóa công việc (xóa mềm)")
    @PutMapping("/soft-delete/{jobId}")
    public ApiResponse<String> softDeleteJobById(@PathVariable("jobId") String jobId) {
        jobService.softDeleteJobById(jobId);
        return ApiResponse.<String>builder().message("Job has been deleted!").build();
    }

    @Operation(summary = "Xóa công việc")
    @DeleteMapping("/{jobId}")
    public ApiResponse<String> deleteJobById(@PathVariable("jobId") String jobId) {
        jobService.deleteJobById(jobId);
        return ApiResponse.<String>builder().message("Job has been deleted!").build();
    }

}
