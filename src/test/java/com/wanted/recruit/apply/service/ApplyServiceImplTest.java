package com.wanted.recruit.apply.service;

import com.wanted.recruit.apply.Apply;
import com.wanted.recruit.apply.dto.ApplyRequest;
import com.wanted.recruit.apply.dto.ApplyResponse;
import com.wanted.recruit.apply.repository.ApplyRepository;
import com.wanted.recruit.common.Company;
import com.wanted.recruit.common.User;
import com.wanted.recruit.common.UserRepository;
import com.wanted.recruit.exception.AlreadyAppliedException;
import com.wanted.recruit.exception.JobPostNotFoundException;
import com.wanted.recruit.exception.UserNotFoundException;
import com.wanted.recruit.jobpost.JobPost;
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
    @DisplayName("공고 지원 성공 시나리오")
    void apply_WhenValid_ShouldSaveApplyAndReturnApplyResponse() {
        User user = new User("유저1");
        Apply apply = new Apply(jobPost, user);

        when(applyRepository.existsByUserIdAndJobPostId(request.getUserId(), request.getJobPostId()))
                .thenReturn(false);
        when(jobPostRepository.findById(request.getJobPostId())).thenReturn(Optional.of(jobPost));
        when(userRepository.findById(request.getUserId())).thenReturn(Optional.of(user));
        when(applyRepository.save(any(Apply.class))).thenReturn(apply);

        ApplyResponse result = applyService.apply(request);
        verify(applyRepository, times(1)).existsByUserIdAndJobPostId(request.getUserId(), request.getJobPostId());
        verify(jobPostRepository, times(1)).findById(request.getJobPostId());
        verify(userRepository, times(1)).findById(request.getUserId());
        verify(applyRepository, times(1)).save(any(Apply.class));
    }

    @Test
    @DisplayName("이미 지원한 공고의 경우: AlreadyAppliedException")
    void apply_WhenAlreadyApplied_ShouldThrowAlreadyAppliedException() {
        when(applyRepository.existsByUserIdAndJobPostId(request.getUserId(), request.getJobPostId()))
                .thenReturn(true);

        assertThrows(AlreadyAppliedException.class, () -> applyService.apply(request));
        verify(applyRepository, times(1)).existsByUserIdAndJobPostId(request.getUserId(), request.getJobPostId());
        verify(jobPostRepository, times(0)).findById(request.getJobPostId());
        verify(userRepository, times(0)).findById(request.getUserId());
        verify(applyRepository, times(0)).save(any(Apply.class));
    }

    @Test
    @DisplayName("해당 채용 공고가 존재하지 않는 경우: JobPostNotFoundException")
    void apply_WhenJobPostNotFound_ShouldThrowJobPostNotFoundException() {
        when(applyRepository.existsByUserIdAndJobPostId(request.getUserId(), request.getJobPostId()))
                .thenReturn(false);
        when(jobPostRepository.findById(request.getJobPostId())).thenReturn(Optional.empty());

        assertThrows(JobPostNotFoundException.class, () -> applyService.apply(request));
        verify(applyRepository, times(1)).existsByUserIdAndJobPostId(request.getUserId(), request.getJobPostId());
        verify(jobPostRepository, times(1)).findById(request.getJobPostId());
        verify(userRepository, times(0)).findById(request.getUserId());
        verify(applyRepository, times(0)).save(any(Apply.class));
    }

    @Test
    @DisplayName("해당 채용 공고가 존재하지 않는 경우: UserNotFoundException")
    void apply_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        when(applyRepository.existsByUserIdAndJobPostId(request.getUserId(), request.getJobPostId()))
                .thenReturn(false);
        when(jobPostRepository.findById(request.getJobPostId())).thenReturn(Optional.of(jobPost));
        when(userRepository.findById(request.getUserId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> applyService.apply(request));
        verify(applyRepository, times(1)).existsByUserIdAndJobPostId(request.getUserId(), request.getJobPostId());
        verify(jobPostRepository, times(1)).findById(request.getJobPostId());
        verify(userRepository, times(1)).findById(request.getUserId());
        verify(applyRepository, times(0)).save(any(Apply.class));
    }
}