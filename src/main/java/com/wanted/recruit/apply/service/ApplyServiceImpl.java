package com.wanted.recruit.apply.service;

import com.wanted.recruit.apply.entity.Apply;
import com.wanted.recruit.apply.dto.ApplyRequest;
import com.wanted.recruit.apply.dto.ApplyResponse;
import com.wanted.recruit.apply.repository.ApplyRepository;
import com.wanted.recruit.jobpost.entity.JobPost;
import com.wanted.recruit.user.entity.User;
import com.wanted.recruit.common.exception.exception.AlreadyAppliedException;
import com.wanted.recruit.common.exception.exception.JobPostNotFoundException;
import com.wanted.recruit.common.exception.exception.UserNotFoundException;
import com.wanted.recruit.jobpost.repository.JobPostRepository;
import com.wanted.recruit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ApplyService 구현체
 */
@Service
@RequiredArgsConstructor
public class ApplyServiceImpl implements ApplyService {
    private final ApplyRepository applyRepository;
    private final JobPostRepository jobPostRepository;
    private final UserRepository userRepository;

    /**
     * 사용자(userId)가 특정 채용 공고(jobPostId)에 지원
     * 지원 여부를 확인, 채용 공고/사용자 정보 조회, 지원 결과 저장
     *
     * @param request 지원 요청 정보
     * @return 지원 결과 및 응답 객체
     * @throws AlreadyAppliedException 이미 지원한 경우
     * @throws JobPostNotFoundException 채용 공고가 존재하지 않는 경우
     * @throws UserNotFoundException 사용자가 존재하지 않는 경우
     */
    @Override
    @Transactional
    public ApplyResponse apply(ApplyRequest request) {
        // 지원 여부 확인
        if (applyRepository.existsByUserIdAndJobPostId(request.getUserId(), request.getJobPostId())) {
            throw new AlreadyAppliedException();
        }

        // 채용 공고 조회
        JobPost jobPost = jobPostRepository.findById(request.getJobPostId()).orElseThrow(JobPostNotFoundException::new);

        // 사용자 조회
        User user = userRepository.findById(request.getUserId()).orElseThrow(UserNotFoundException::new);

        // 지원 내역 저장
        Apply apply = applyRepository.save(new Apply(jobPost, user));

        return new ApplyResponse(apply);
    }
}
