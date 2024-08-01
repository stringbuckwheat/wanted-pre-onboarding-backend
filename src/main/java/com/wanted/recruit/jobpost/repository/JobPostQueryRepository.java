package com.wanted.recruit.jobpost.repository;

import com.wanted.recruit.jobpost.dto.JobPostResponse;

import java.util.List;

public interface JobPostQueryRepository {
    List<JobPostResponse> search(String searchQuery);
    List<Long> getOtherJobPost(Long companyId, Long jobPostId);
}
