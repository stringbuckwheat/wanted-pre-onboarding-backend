package com.wanted.recruit.apply.repository;

/**
 * Apply 엔티티에 대한 QueryDsl 리파지토리 인터페이스
 */
public interface ApplyQueryRepository {

    /**
     * 채용공고 지원 여부 확인
     *
     * @param userId  사용자 ID
     * @param jobPostId  채용 공고 ID
     * @return 이미 지원했을 시 true, 아니면 false
     */
    boolean existsByUserIdAndJobPostId(Long userId, Long jobPostId);
}
