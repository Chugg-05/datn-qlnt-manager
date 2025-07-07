package com.example.datn_qlnt_manager.dto.request.job;

import com.example.datn_qlnt_manager.common.JobObjectType;
import com.example.datn_qlnt_manager.common.JobPriorityLevel;
import com.example.datn_qlnt_manager.common.JobStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobCreationRequest {
    @NotBlank(message = "BUILDING_ID_NOT_FOUND")
    String buildingId;
    String jobCode;
    @NotBlank(message = "INVALID_TITLE_BLANK")
    String title;
    String description;
    @NotNull(message = "INVALID_JOB_PRIORITY_LEVEL_NULL")
    JobPriorityLevel jobPriorityLevel;
    JobStatus jobStatus;
    @NotNull(message = "INVALID_COMPLETION_DEADLINE_NULL")
    @FutureOrPresent(message = "INVALID_COMPLETION_DEADLINE_PAST")
    Date completionDeadline;
    @NotNull(message = "INVALID_JOB_OBJECT_TYPE_NULL")
    JobObjectType jobObjectType;
}
