package com.wanted.recruit.apply.repository;

public interface ApplyQueryRepository {
    boolean existsByUserIdAndJobPostId(Long userId, Long jobPostId);
}
