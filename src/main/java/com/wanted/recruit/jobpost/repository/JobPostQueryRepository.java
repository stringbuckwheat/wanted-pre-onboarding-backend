package com.wanted.recruit.jobpost.repository;

import com.wanted.recruit.jobpost.dto.JobPostResponse;

import java.util.List;

/**
 * JobPost 엔티티에 대한 QueryDsl 리파지토리 인터페이스
 */
public interface JobPostQueryRepository {
    /**
     * 검색어를 통해 채용 공고 조회
     *
     * @param searchQuery 검색어
     * @return 검색어가 포함된 채용 공고 리스트
     */
    List<JobPostResponse> search(String searchQuery);

    /**
     * 특정 회사의 다른 채용 공고 id 목록 조회
     * 특정 채용 공고(a)의 추가적인 정보로 제공 -> 즉, 채용정보 a는 제외
     *
     * @param companyId 회사 Id
     * @param jobPostId 채용 공고 id
     * @return 회사의 다른 채용 공고 ID 목록
     */
    List<Long> getOtherJobPost(Long companyId, Long jobPostId);
}
