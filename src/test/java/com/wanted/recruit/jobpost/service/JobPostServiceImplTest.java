package com.wanted.recruit.jobpost.service;

import com.wanted.recruit.common.Company;
import com.wanted.recruit.common.CompanyRepository;
import com.wanted.recruit.exception.CompanyNotFoundException;
import com.wanted.recruit.exception.JobPostNotFoundException;
import com.wanted.recruit.jobpost.JobPost;
import com.wanted.recruit.jobpost.dto.*;
import com.wanted.recruit.jobpost.repository.JobPostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobPostServiceImplTest {
    @Mock
    private JobPostRepository jobPostRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private JobPostServiceImpl jobPostService;

    private Company company;
    private JobPost jobPost;

    @BeforeEach
    void setUp() {
        company = Company.builder().name("원티드").nation("한국").region("서울").build();
        jobPost = JobPost.builder()
                .position("신입 백엔드 개발자")
                .reward(500000)
                .techStack("Java/Spring")
                .content("공고 상세 내용")
                .company(company)
                .build();
    }

    @Test
    @DisplayName("모든 값이 유효한 경우, JobPost 저장 성공")
    void save_whenAllValid_ShouldReturnJobPostResponse() {
        // given
        JobPostRequest jobPostRequest = JobPostRequest.builder()
                .companyId(1L)
                .position("신입 백엔드 개발자")
                .reward(500000)
                .techStack("Java/Spring")
                .content("공고 상세 내용")
                .build();

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(jobPostRepository.save(any(JobPost.class))).thenReturn(jobPost);

        // when
        JobPostResponse result = jobPostService.save(jobPostRequest);

        // then
        assertNotNull(result);
        assertEquals(jobPost.getPosition(), result.getPosition());
        verify(companyRepository, times(1)).findById(1L);
        verify(jobPostRepository, times(1)).save(any(JobPost.class));
    }

    @Test
    @DisplayName("회사 정보를 찾을 수 없는 경우, CompanyNotFoundException")
    void save_WhenCompanyNotFound_ThrowCompanyNotFoundException() {
        // given
        JobPostRequest jobPostRequest = JobPostRequest.builder()
                .companyId(1L)
                .position("신입 백엔드 개발자")
                .reward(500000)
                .techStack("Java/Spring")
                .content("공고 상세 내용")
                .build();

        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        // when, then
        assertThrows(CompanyNotFoundException.class, () -> jobPostService.save(jobPostRequest));
        verify(companyRepository, times(1)).findById(1L);
        verify(jobPostRepository, times(0)).save(any(JobPost.class));
    }

    @Test
    @DisplayName("모든 값이 유효한 경우, JobPost 수정 성공")
    void update_WhenAllValid_ShouldReturnJobPostResponse() {
        // Given
        JobPostUpdateRequest jobPostUpdateRequest = JobPostUpdateRequest.builder()
                .position("신입 Java 백엔드 개발자") // 수정
                .reward(500000)
                .techStack("Java/Spring, JPA, QueryDsl") // 수정
                .content("공고 상세 내용 수정") // 수정
                .build();

        when(jobPostRepository.findById(1L)).thenReturn(Optional.of(jobPost));

        JobPostResponse response = jobPostService.update(jobPostUpdateRequest, 1L);

        assertNotNull(response);
        assertEquals(jobPostUpdateRequest.getPosition(), response.getPosition());
        assertEquals(jobPostUpdateRequest.getTechStack(), response.getTechStack());
        assertEquals(jobPostUpdateRequest.getContent(), response.getContent());
        verify(jobPostRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("공고 정보를 찾을 수 없는 경우, JobPostNotFoundException")
    void update_WhenJobPostNotFound_ThrowJobPostNotFoundException() {
        // given
        JobPostUpdateRequest jobPostUpdateRequest = JobPostUpdateRequest.builder()
                .position("신입 Java 백엔드 개발자")
                .reward(500000)
                .techStack("Java/Spring, JPA, QueryDsl")
                .content("공고 상세 내용 수정")
                .build();

        when(jobPostRepository.findById(1L)).thenReturn(Optional.empty());

        // when, then
        assertThrows(JobPostNotFoundException.class, () -> jobPostService.update(jobPostUpdateRequest, 1L));
        verify(jobPostRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("모든 값이 유효한 경우, JobPost 삭제 성공")
    void delete_WhenAllValid_ReturnNoContent() {
        // given
        when(jobPostRepository.findById(1L)).thenReturn(Optional.of(jobPost));

        jobPostService.delete(1L);

        verify(jobPostRepository, times(1)).findById(1L);
        verify(jobPostRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("공고 정보를 찾을 수 없는 경우, JobPostNotFoundException")
    void delete_WhenJobPostNotFound_ThrowJobPostNotFoundException() {
        // given
        when(jobPostRepository.findById(1L)).thenReturn(Optional.empty());

        // when, then
        assertThrows(JobPostNotFoundException.class, () -> jobPostService.delete(1L));
        verify(jobPostRepository, times(1)).findById(1L);
        verify(jobPostRepository, times(0)).deleteById(1L);
    }

    @Test
    @DisplayName("검색어 조건이 넘어오지 않은 경우, 공고 전체 리스트 반환")
    void getList_WhenNoSearchQuery_ShouldReturnEntireList() {
        // given
        when(jobPostRepository.findAll()).thenReturn(Arrays.asList(jobPost));

        // when
        List<JobPostResponse> responses = jobPostService.getList(null);

        // then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(jobPostRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("검색어가 넘어온 경우, 검색된 결과를 반환")
    void getList_WhenHasSearchQuery_ShouldReturnSearchedList() {
        // given
        String searchQuery = "원티드";

        JobPostResponse searchedJob = new JobPostResponse(jobPost);

        when(jobPostRepository.search(searchQuery)).thenReturn(Arrays.asList(searchedJob));

        // when
        List<JobPostResponse> responses = jobPostService.getList(searchQuery);

        // then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(jobPostRepository, times(1)).search(searchQuery);
    }

    @Test
    @DisplayName("모든 값이 유효한 경우, 공고 상세정보 리턴")
    void getDetail_WhenAllValid_ShouldReturnJobPostDetail() {
        // given
        Long companyId = 1L;
        Long jobPostId = 2L;

        Company company = mock(Company.class);
        when(company.getId()).thenReturn(companyId);

        JobPost jobPost = mock(JobPost.class);
        when(jobPost.getId()).thenReturn(jobPostId);
        when(jobPost.getCompany()).thenReturn(company);
        when(jobPostRepository.findById(jobPostId)).thenReturn(Optional.of(jobPost));
        when(jobPostRepository.getOtherJobPost(companyId, jobPostId)).thenReturn(Arrays.asList(3L, 4L));

        // when
        JobPostDetail detail = jobPostService.getDetail(jobPostId);

        // then
        assertNotNull(detail);
        assertEquals(jobPostId, detail.getJobPostId());
        verify(jobPostRepository, times(1)).findById(jobPostId);
        verify(jobPostRepository, times(1)).getOtherJobPost(companyId, jobPostId);
    }

    @Test
    @DisplayName("공고 정보를 찾을 수 없는 경우, JobPostNotFoundException")
    void getDetail_WhenJobPostNotFound_ThrowJobPostNotFoundException() {
        when(jobPostRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(JobPostNotFoundException.class, () -> jobPostService.getDetail(1L));
        verify(jobPostRepository, times(1)).findById(1L);
    }
}