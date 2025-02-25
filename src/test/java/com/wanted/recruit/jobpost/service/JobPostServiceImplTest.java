package com.wanted.recruit.jobpost.service;

import com.wanted.recruit.common.exception.exception.InvalidSearchQueryException;
import com.wanted.recruit.company.entity.Company;
import com.wanted.recruit.company.repository.CompanyRepository;
import com.wanted.recruit.common.exception.exception.CompanyNotFoundException;
import com.wanted.recruit.common.exception.exception.JobPostNotFoundException;
import com.wanted.recruit.jobpost.entity.JobPost;
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
@DisplayName("JobPostService 테스트")
class JobPostServiceImplTest {
    @Mock
    private JobPostRepository jobPostRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private JobPostServiceImpl jobPostService;

    private Company company;
    private JobPost jobPost;
    private JobPostRequest jobPostRequest;
    private JobPostUpdateRequest jobPostUpdateRequest;

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

        jobPostRequest = JobPostRequest.builder()
                .companyId(1L)
                .position("신입 백엔드 개발자")
                .reward(500000)
                .techStack("Java/Spring")
                .content("공고 상세 내용")
                .build();

        jobPostUpdateRequest = JobPostUpdateRequest.builder()
                .position("신입 Java 백엔드 개발자") // 수정
                .reward(500000)
                .techStack("Java/Spring, JPA, QueryDsl") // 수정
                .content("공고 상세 내용 수정") // 수정
                .build();
    }

    @Test
    @DisplayName("채용 공고 저장: 성공 시나리오")
    void save_whenAllValid_ShouldReturnJobPostResponse() {
        // 회사 정보 존재
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

        when(jobPostRepository.save(any(JobPost.class))).thenReturn(jobPost);

        JobPostResponse result = jobPostService.save(jobPostRequest);

        assertNotNull(result);
        assertEquals(jobPost.getPosition(), result.getPosition()); // 필드 검증

        // 메소드 호출 횟수
        verify(companyRepository, times(1)).findById(1L);
        verify(jobPostRepository, times(1)).save(any(JobPost.class));
    }

    @Test
    @DisplayName("채용 공고 저장: 회사 정보를 찾을 수 없는 경우, CompanyNotFoundException")
    void save_WhenCompanyNotFound_ThrowCompanyNotFoundException() {
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        // when, then
        assertThrows(CompanyNotFoundException.class, () -> jobPostService.save(jobPostRequest));
        verify(companyRepository, times(1)).findById(1L);
        verify(jobPostRepository, times(0)).save(any(JobPost.class));
    }

    @Test
    @DisplayName("채용 공고 수정: 모든 값이 유효한 경우, JobPost 수정 성공")
    void update_WhenAllValid_ShouldReturnJobPostResponse() {
        // Given
        when(jobPostRepository.findById(1L)).thenReturn(Optional.of(jobPost));

        JobPostResponse response = jobPostService.update(jobPostUpdateRequest, 1L);

        assertNotNull(response);

        // 수정한 필드 확인
        assertEquals(jobPostUpdateRequest.getPosition(), response.getPosition());
        assertEquals(jobPostUpdateRequest.getTechStack(), response.getTechStack());
        assertEquals(jobPostUpdateRequest.getContent(), response.getContent());

        verify(jobPostRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("채용 공고 수정: 공고 정보를 찾을 수 없는 경우, JobPostNotFoundException")
    void update_WhenJobPostNotFound_ThrowJobPostNotFoundException() {
        // 해당 공고 정보 없음
        when(jobPostRepository.findById(1L)).thenReturn(Optional.empty());

        // JobPostNotFoundException 검증
        assertThrows(JobPostNotFoundException.class, () -> jobPostService.update(jobPostUpdateRequest, 1L));
        verify(jobPostRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("채용 공고 삭제: 성공 시나리오")
    void delete_WhenAllValid_ReturnNoContent() {
        // 해당 공고 정보 존재
        when(jobPostRepository.existsById(1L)).thenReturn(true);

        jobPostService.delete(1L);

        verify(jobPostRepository, times(1)).existsById(1L);
        verify(jobPostRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("채용 공고 삭제: 공고 정보를 찾을 수 없는 경우, JobPostNotFoundException")
    void delete_WhenJobPostNotFound_ThrowJobPostNotFoundException() {
        // 해당 공고 정보 없음
        when(jobPostRepository.existsById(1L)).thenReturn(false);

        assertThrows(JobPostNotFoundException.class, () -> jobPostService.delete(1L));
        verify(jobPostRepository, times(1)).existsById(1L);
        verify(jobPostRepository, times(0)).deleteById(1L);
    }

    @Test
    @DisplayName("채용 공고 목록: 검색어가 없는 경우, 전체 공고 리스트 반환")
    void getList_WhenNoSearchQuery_ShouldReturnEntireList() {
        String searchQuery = null; // 검색어 없음
        when(jobPostRepository.findAll()).thenReturn(Arrays.asList(jobPost));

        List<JobPostResponse> responses = jobPostService.getAll();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(jobPostRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("채용 공고 목록: 검색어가 있는 경우, 검색된 결과 반환")
    void getList_WhenHasSearchQuery_ShouldReturnSearchedList() {
        String searchQuery = "원티드"; // 검색어 있음

        JobPostResponse searchedJob = new JobPostResponse(jobPost); // 응답 객체

        when(jobPostRepository.search(searchQuery)).thenReturn(Arrays.asList(searchedJob));

        List<JobPostResponse> responses = jobPostService.search(searchQuery);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(jobPostRepository, times(1)).search(searchQuery);
    }

    @Test
    @DisplayName("채용 공고 목록: 검색어 유효성 검사 통과 못하는 경우, InvalidSearchQueryException")
    void getList_WhenInvalidSearchQuery_ShouldThrowInvalidSearchQueryException() {
        String searchQuery = "$원티드"; // 검색어 유효성 검사 통과 X
        assertThrows(InvalidSearchQueryException.class, () -> jobPostService.search(searchQuery));
        verify(jobPostRepository, times(0)).search(searchQuery);
    }

    @Test
    @DisplayName("공고 상세: 성공 시나리오")
    void getDetail_WhenAllValid_ShouldReturnJobPostDetail() {
        Long companyId = 1L;
        Long jobPostId = 2L;

        // id 필드도 모킹한 Company 엔티티
        Company company = mock(Company.class);
        when(company.getId()).thenReturn(companyId);

        // id 필드도 모킹한 JobPost 엔티티
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
    @DisplayName("공고 상세: 공고 정보를 찾을 수 없는 경우, JobPostNotFoundException")
    void getDetail_WhenJobPostNotFound_ThrowJobPostNotFoundException() {
        when(jobPostRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(JobPostNotFoundException.class, () -> jobPostService.getDetail(1L));
        verify(jobPostRepository, times(1)).findById(1L);
    }
}