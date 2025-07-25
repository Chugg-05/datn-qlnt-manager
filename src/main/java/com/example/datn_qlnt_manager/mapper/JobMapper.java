package com.example.datn_qlnt_manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.datn_qlnt_manager.dto.request.job.JobCreationRequest;
import com.example.datn_qlnt_manager.dto.request.job.JobUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.job.JobResponse;
import com.example.datn_qlnt_manager.entity.Job;

@Mapper(componentModel = "spring")
public interface JobMapper {

    @Mapping(target = "jobCode", ignore = true)
    @Mapping(target = "jobStatus", ignore = true)
    Job toJob(JobCreationRequest request);

    @Mapping(source = "building.buildingName", target = "buildingName")
    JobResponse toJobResponse(Job job);

    void updateJob(@MappingTarget Job job, JobUpdateRequest jobUpdateRequest);
}
