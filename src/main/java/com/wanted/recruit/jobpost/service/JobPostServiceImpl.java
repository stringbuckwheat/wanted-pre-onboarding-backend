package com.wanted.recruit.jobpost.service;

import com.wanted.recruit.common.exception.exception.InvalidSearchQueryException;
import com.wanted.recruit.company.entity.Company;
import com.wanted.recruit.common.exception.exception.CompanyNotFoundException;
import com.wanted.recruit.common.exception.exception.JobPostNotFoundException;
import com.wanted.recruit.company.repository.CompanyRepository;
import com.wanted.recruit.jobpost.entity.JobPost;
import com.wanted.recruit.jobpost.dto.JobPostDetail;
import com.wanted.recruit.jobpost.dto.JobPostRequest;
import com.wanted.recruit.jobpost.dto.JobPostResponse;
import com.wanted.recruit.jobpost.dto.JobPostUpdateRequest;
import com.wanted.recruit.jobpost.repository.JobPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@Link JobPostService} 구현체
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JobPostServiceImpl implements JobPostService {
    private final JobPostRepository jobPostRepository;
    private final CompanyRepository companyRepository;

    // 검색어 검사용 정규표현식
    // 알파벳, 숫자, 한글, 공백, 하이픈(-), 언더스코어(_), 콤마(,), 슬래시(/), 괄호((), {}, [])만 허용
    private static final String SAFE_QUERY_PATTERN = "^[a-zA-Z0-9가-힣\\s\\-_,./()\\[\\]{}]*$";


    /**
     * 전체 채용 공고 리스트
     *
     * @return 전체 채용 공고 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<JobPostResponse> getAll() {
        return jobPostRepository.findAll().stream()
                .map(JobPostResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 검색된 채용 공고 리스트
     *
     * @param searchQuery 검색어
     * @return 검색된 채용 공고 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<JobPostResponse> search(String searchQuery) {
        // 검색어 유효성 검사
        if (!searchQuery.matches(SAFE_QUERY_PATTERN)) {
            throw new InvalidSearchQueryException();
        }

        return jobPostRepository.search(searchQuery);
    }

    /**
     * 채용 공고의 세부 정보
     *
     * @param id 조회할 채용 공고의 ID
     * @return 채용 공고의 세부 정보
     * @throws JobPostNotFoundException 해당 채용 공고가 존재하지 않는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public JobPostDetail getDetail(Long id) {
        JobPost jobPost = jobPostRepository.findById(id).orElseThrow(JobPostNotFoundException::new);
        List<Long> otherJobPost = jobPostRepository.getOtherJobPost(jobPost.getCompany().getId(), jobPost.getId());

        return new JobPostDetail(jobPost, otherJobPost);
    }

    /**
     * 새로운 채용 공고 저장
     *
     * @param request 저장할 채용 공고의 요청 데이터
     * @return 저장된 채용 공고의 응답 데이터
     */
    @Override
    @Transactional
    public JobPostResponse save(JobPostRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(CompanyNotFoundException::new);

        JobPost jobPost = jobPostRepository.save(request.toEntity(company));

        return new JobPostResponse(jobPost);
    }

    /**
     * 기존 채용 공고 업데이트
     *
     * @param request 업데이트할 채용 공고의 요청 데이터
     * @param id      업데이트할 채용 공고 ID
     * @return 업데이트된 채용 공고의 응답 데이터
     * @throws JobPostNotFoundException 해당 채용 공고가 존재하지 않는 경우
     */
    @Override
    @Transactional
    public JobPostResponse update(JobPostUpdateRequest request, Long id) {
        JobPost jobPost = jobPostRepository.findById(id).orElseThrow(JobPostNotFoundException::new);

        jobPost.update(request.getPosition(), request.getReward(), request.getContent(), request.getTechStack());

        return new JobPostResponse(jobPost);
    }

    /**
     * 채용 공고 삭제
     *
     * @param id 삭제할 채용 공고 ID
     * @throws JobPostNotFoundException 해당 채용 공고가 존재하지 않는 경우
     */
    @Override
    @Transactional
    public void delete(Long id) {
        // 해당 엔티티가 존재하는지 확인 후 삭제
        if (!jobPostRepository.existsById(id)) {
            throw new JobPostNotFoundException();
        }

        jobPostRepository.deleteById(id);
    }
}
