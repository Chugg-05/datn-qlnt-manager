package com.example.datn_qlnt_manager.dto.request.job;

import com.example.datn_qlnt_manager.common.JobObjectType;
import com.example.datn_qlnt_manager.common.JobPriorityLevel;
import com.example.datn_qlnt_manager.common.JobStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobCreationRequest {
    String buildingId;
    String jobCode;
    String title;
    String description;
    JobPriorityLevel jobPriorityLevel;
    JobStatus jobStatus;
    Date completionDeadline;
    JobObjectType jobObjectType;
}
