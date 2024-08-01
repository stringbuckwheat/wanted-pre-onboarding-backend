package com.wanted.recruit.jobpost.repository;

import com.wanted.recruit.jobpost.JobPost;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface JobPostRepository extends Repository<JobPost, Long>, JobPostQueryRepository {
    JobPost save(JobPost jobPost);

    Optional<JobPost> findById(Long id);

    void deleteById(Long id);

    List<JobPost> findAll();
}
