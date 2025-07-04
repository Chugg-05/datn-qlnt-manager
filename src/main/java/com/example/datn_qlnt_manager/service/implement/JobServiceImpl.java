package com.example.datn_qlnt_manager.service.implement;


import com.example.datn_qlnt_manager.mapper.JobMapper;
import com.example.datn_qlnt_manager.repository.JobRepository;
import com.example.datn_qlnt_manager.service.JobService;
import org.springframework.stereotype.Service;

@Service
public class JobServiceImpl implements JobService {
    JobRepository jobRepository;
    JobMapper jobMapper;


//    @Override
//    public JobResponse createJob (JobCreationRequest request){
//
//        Job job = jobMapper.toJob(request);
//
//    }

}
