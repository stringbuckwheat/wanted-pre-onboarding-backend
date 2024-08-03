package com.wanted.recruit.jobpost.repository;

import com.wanted.recruit.jobpost.entity.JobPost;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 채용 공고 엔티티 용 Spring Data JPA Repository 인터페이스
 */
public interface JobPostRepository extends Repository<JobPost, Long>, JobPostQueryRepository {
    JobPost save(JobPost jobPost);

    Optional<JobPost> findById(Long id);

    void deleteById(Long id);

    List<JobPost> findAll();
}
