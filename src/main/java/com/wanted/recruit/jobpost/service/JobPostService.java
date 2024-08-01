package com.wanted.recruit.jobpost.service;

import com.wanted.recruit.jobpost.dto.JobPostDetail;
import com.wanted.recruit.jobpost.dto.JobPostRequest;
import com.wanted.recruit.jobpost.dto.JobPostResponse;
import com.wanted.recruit.jobpost.dto.JobPostUpdateRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface JobPostService {
    JobPostResponse save(JobPostRequest request);

    @Transactional
    JobPostResponse update(JobPostUpdateRequest request, Long id);

    void delete(Long id);

    List<JobPostResponse> getAll();

    List<JobPostResponse> search(String searchParam);

    JobPostDetail getDetail(Long id);
}
