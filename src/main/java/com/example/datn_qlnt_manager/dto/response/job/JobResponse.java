package com.example.datn_qlnt_manager.dto.response.job;

import java.time.Instant;
import java.util.Date;

import com.example.datn_qlnt_manager.common.JobObjectType;
import com.example.datn_qlnt_manager.common.JobPriorityLevel;
import com.example.datn_qlnt_manager.common.JobStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobResponse {
    String id;
    String buildingName;
    String jobCode;
    String title;
    String description;
    JobPriorityLevel jobPriorityLevel;
    JobStatus jobStatus;
    Date completionDeadline;
    JobObjectType jobObjectType;
    Instant createdAt;
    Instant updatedAt;
}
