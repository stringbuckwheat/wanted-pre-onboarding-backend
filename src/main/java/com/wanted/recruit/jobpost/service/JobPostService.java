package com.wanted.recruit.jobpost.service;

import com.wanted.recruit.common.exception.exception.JobPostNotFoundException;
import com.wanted.recruit.jobpost.dto.JobPostDetail;
import com.wanted.recruit.jobpost.dto.JobPostRequest;
import com.wanted.recruit.jobpost.dto.JobPostResponse;
import com.wanted.recruit.jobpost.dto.JobPostUpdateRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 채용 공고 서비스 인터페이스
 */
public interface JobPostService {
    /**
     * 전체 채용 공고 리스트
     * @return 전체 채용 공고 리스트
     */
    List<JobPostResponse> getAll();

    /**
     * 검색된 채용 공고 리스트
     * @param searchQuery 검색어
     * @return 검색된 채용 공고 리스트
     */
    List<JobPostResponse> search(String searchQuery);

    /**
     * 채용 공고의 세부 정보
     *
     * @param id 조회할 채용 공고의 ID
     * @return 채용 공고의 세부 정보
     * @throws JobPostNotFoundException 해당 채용 공고가 존재하지 않는 경우
     */
    JobPostDetail getDetail(Long id);

    /**
     * 새로운 채용 공고 저장
     *
     * @param request 저장할 채용 공고의 요청 데이터
     * @return 저장된 채용 공고의 응답 데이터
     */
    JobPostResponse save(JobPostRequest request);

    /**
     * 기존 채용 공고 업데이트
     *
     * @param request 업데이트할 채용 공고의 요청 데이터
     * @param id      업데이트할 채용 공고 ID
     * @return 업데이트된 채용 공고의 응답 데이터
     * @throws JobPostNotFoundException 해당 채용 공고가 존재하지 않는 경우
     */
    @Transactional
    JobPostResponse update(JobPostUpdateRequest request, Long id);

    /**
     * 채용 공고 삭제
     *
     * @param id 삭제할 채용 공고 ID
     * @throws JobPostNotFoundException 해당 채용 공고가 존재하지 않는 경우
     */
    void delete(Long id);
}
