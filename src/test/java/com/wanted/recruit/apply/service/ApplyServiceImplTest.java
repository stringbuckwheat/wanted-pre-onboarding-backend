package com.wanted.recruit.apply.service;

import com.wanted.recruit.apply.entity.Apply;
import com.wanted.recruit.apply.dto.ApplyRequest;
import com.wanted.recruit.apply.dto.ApplyResponse;
import com.wanted.recruit.apply.repository.ApplyRepository;
import com.wanted.recruit.company.entity.Company;
import com.wanted.recruit.user.entity.User;
import com.wanted.recruit.user.repository.UserRepository;
import com.wanted.recruit.common.exception.exception.AlreadyAppliedException;
import com.wanted.recruit.common.exception.exception.JobPostNotFoundException;
import com.wanted.recruit.common.exception.exception.UserNotFoundException;
import com.wanted.recruit.jobpost.entity.JobPost;
import com.wanted.recruit.jobpost.repository.JobPostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApplyService 테스트")
class ApplyServiceImplTest {
    @Mock
    private ApplyRepository applyRepository;

    @Mock
    private JobPostRepository jobPostRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplyServiceImpl applyService;

    private ApplyRequest request;
    private JobPost jobPost;


    /**
     * 기본적인 데이터 모킹
     */
    @BeforeEach
    void setUp() {
        request = new ApplyRequest(1L, 2L);

        Company company = Company.builder().name("원티드").nation("한국").region("서울").build();

        jobPost = JobPost.builder()
                .position("신입 백엔드 개발자")
                .reward(500000)
                .techStack("Java/Spring")
                .content("공고 상세 내용")
                .company(company)
                .build();
    }

    @Test
    @DisplayName("공고 지원: 성공 시나리오")
    void apply_WhenValid_ShouldSaveApplyAndReturnApplyResponse() {
        User user = new User("유저1");
        Apply apply = new Apply(jobPost, user);

        // 해당 공고에 지원 내역 없음
        when(applyRepository.existsByUserIdAndJobPostId(request.getUserId(), request.getJobPostId()))
                .thenReturn(false);

        // 지원 공고 존재
        when(jobPostRepository.findById(request.getJobPostId())).thenReturn(Optional.of(jobPost));

        // 사용자 정보 존재
        when(userRepository.findById(request.getUserId())).thenReturn(Optional.of(user));

        // 지원 내역 저장
        when(applyRepository.save(any(Apply.class))).thenReturn(apply);

        ApplyResponse result = applyService.apply(request);

        // 호출 횟수 확인
        verify(applyRepository, times(1)).existsByUserIdAndJobPostId(request.getUserId(), request.getJobPostId());
        verify(jobPostRepository, times(1)).findById(request.getJobPostId());
        verify(userRepository, times(1)).findById(request.getUserId());
        verify(applyRepository, times(1)).save(any(Apply.class));
    }

    @Test
    @DisplayName("공고 지원: 이미 지원한 공고 -> AlreadyAppliedException")
    void apply_WhenAlreadyApplied_ShouldThrowAlreadyAppliedException() {
        // 이미 지원한 공고
        when(applyRepository.existsByUserIdAndJobPostId(request.getUserId(), request.getJobPostId()))
                .thenReturn(true);

        // AlreadyAppliedException 발생 여부 확인
        assertThrows(AlreadyAppliedException.class, () -> applyService.apply(request));

        // 메소드 호출 횟수 확인
        verify(applyRepository, times(1)).existsByUserIdAndJobPostId(request.getUserId(), request.getJobPostId());
        verify(jobPostRepository, times(0)).findById(request.getJobPostId());
        verify(userRepository, times(0)).findById(request.getUserId());
        verify(applyRepository, times(0)).save(any(Apply.class));
    }

    @Test
    @DisplayName("공고 지원: 해당 채용 공고가 존재하지 않는 경우 -> JobPostNotFoundException")
    void apply_WhenJobPostNotFound_ShouldThrowJobPostNotFoundException() {
        // 지원 내역 없음
        when(applyRepository.existsByUserIdAndJobPostId(request.getUserId(), request.getJobPostId()))
                .thenReturn(false);

        // 채용 공고 존재하지 않음
        when(jobPostRepository.findById(request.getJobPostId())).thenReturn(Optional.empty());

        // JobPostNotFoundException 발생 여부 확인
        assertThrows(JobPostNotFoundException.class, () -> applyService.apply(request));

        // 메소드 호출 횟수 확인
        verify(applyRepository, times(1)).existsByUserIdAndJobPostId(request.getUserId(), request.getJobPostId());
        verify(jobPostRepository, times(1)).findById(request.getJobPostId());
        verify(userRepository, times(0)).findById(request.getUserId());
        verify(applyRepository, times(0)).save(any(Apply.class));
    }

    @Test
    @DisplayName("공고 지원: 해당 사용자가 존재하지 않는 경우 -> UserNotFoundException")
    void apply_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        // 지원 내역 없음
        when(applyRepository.existsByUserIdAndJobPostId(request.getUserId(), request.getJobPostId()))
                .thenReturn(false);

        // 채용 공고 존재
        when(jobPostRepository.findById(request.getJobPostId())).thenReturn(Optional.of(jobPost));

        // 사용자 정보 존재하지 않음
        when(userRepository.findById(request.getUserId())).thenReturn(Optional.empty());

        // UserNotFoundException 발생 여부 확인
        assertThrows(UserNotFoundException.class, () -> applyService.apply(request));

        // 메소드 호출 횟수 확인
        verify(applyRepository, times(1)).existsByUserIdAndJobPostId(request.getUserId(), request.getJobPostId());
        verify(jobPostRepository, times(1)).findById(request.getJobPostId());
        verify(userRepository, times(1)).findById(request.getUserId());
        verify(applyRepository, times(0)).save(any(Apply.class));
    }
}