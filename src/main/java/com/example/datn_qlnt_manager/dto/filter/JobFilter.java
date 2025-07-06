package com.example.datn_qlnt_manager.dto.filter;

import com.example.datn_qlnt_manager.common.JobObjectType;
import com.example.datn_qlnt_manager.common.JobPriorityLevel;
import com.example.datn_qlnt_manager.common.JobStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobFilter {
    String buildingId;
    String query;
    JobPriorityLevel jobPriorityLevel;
    JobStatus jobStatus;
    JobObjectType jobObjectType;

}
