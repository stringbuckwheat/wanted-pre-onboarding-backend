package com.wanted.recruit.jobpost.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.recruit.common.exception.exception.InvalidSearchQueryException;
import com.wanted.recruit.company.entity.Company;
import com.wanted.recruit.common.exception.exception.JobPostNotFoundException;
import com.wanted.recruit.jobpost.entity.JobPost;
import com.wanted.recruit.jobpost.dto.*;
import com.wanted.recruit.jobpost.service.JobPostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JobPostController.class)
@DisplayName("JobPostController 테스트")
class JobPostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobPostService jobPostService;

    @Autowired
    private ObjectMapper objectMapper;

    private JobPostRequest jobPostRequest;
    private JobPostResponse jobPostResponse;
    private JobPostUpdateRequest jobPostUpdateRequest;
    private JobPostDetail jobPostDetail;

    @BeforeEach
    void setUp() {
        CompanyResponse mockCompany = CompanyResponse.builder()
                .companyId(1L)
                .name("원티드")
                .nation("한국")
                .region("서울")
                .build();

        jobPostRequest = JobPostRequest.builder()
                .companyId(1L)
                .position("신입 백엔드 개발자")
                .reward(50 * 10000)
                .techStack("Java/Spring")
                .content("공고 상세 내용")
                .build();

        jobPostResponse = JobPostResponse.builder()
                .jobPostId(2L)
                .position("신입 백엔드 개발자")
                .reward(50 * 10000)
                .techStack("Java/Spring")
                .createDate(LocalDateTime.now().minusHours(3))
                .company(mockCompany)
                .build();

        jobPostUpdateRequest = JobPostUpdateRequest.builder()
                .position("신입 Java 백엔드 개발자")
                .reward(50 * 10000)
                .techStack("Java/Spring, JPA, QueryDsl")
                .content("공고 상세 내용 수정")
                .build();

        JobPost mockJobPost = JobPost.builder()
                .position("신입 백엔드 개발자")
                .content("공고 상세 내용")
                .reward(50 * 10000)
                .techStack("Java/Spring")
                .company(Company.builder().name("원티드").nation("한국").region("서울").build())
                .build();

        jobPostDetail = new JobPostDetail(mockJobPost, Collections.emptyList());
    }

    @Test
    @DisplayName("채용 공고 저장: 성공 시나리오(201)")
    void save_WhenValid_ShouldReturnSavedJobPostWith201() throws Exception {
        when(jobPostService.save(any(JobPostRequest.class))).thenReturn(jobPostResponse);

        mockMvc.perform(post("/job")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobPostRequest)))
                .andExpect(status().isCreated()) // 상태코드 확인
                .andExpect(jsonPath("$.jobPostId").value(jobPostResponse.getJobPostId())) // 필드값 검증
                .andExpect(jsonPath("$.position").value(jobPostResponse.getPosition()));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidJobPostRequestProvider.class)
    @DisplayName("채용 공고 저장: 요청 데이터가 불충분한 경우, MethodArgumentNotValidException(400)")
    void save_WhenRequestDataInvalid_ShouldThrowMethodArgumentNotValidExceptionWith400(JobPostRequest invalidRequest) throws Exception {
        mockMvc.perform(post("/job")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()) // 상태 코드 확인
                .andExpect(jsonPath("$.title").value("MethodArgumentNotValidException")); // 필드값 검증
        ;
    }

    @Test
    @DisplayName("채용 공고 수정: 성공 시나리오(200)")
    void update_whenAllValid_ShouldReturnUpdatedJobPost() throws Exception {
        when(jobPostService.update(any(JobPostUpdateRequest.class), anyLong())).thenReturn(jobPostResponse);

        mockMvc.perform(put("/job/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobPostUpdateRequest)))
                .andExpect(status().isOk()) // 상태코드
                .andExpect(jsonPath("$.jobPostId").value(jobPostResponse.getJobPostId()))
                .andExpect(jsonPath("$.position").value(jobPostResponse.getPosition()));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidJobPostUpdateRequestProvider.class)
    @DisplayName("채용 공고 수정: 요청 데이터가 불충분한 경우, MethodArgumentNotValidException(400)")
    void update_WhenRequestDataInvalid_ShouldThrowMethodArgumentNotValidExceptionWith400(JobPostUpdateRequest invalidRequest) throws Exception {
        mockMvc.perform(put("/job/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("MethodArgumentNotValidException"));
    }

    @Test
    @DisplayName("채용 공고 수정: 공고 정보를 찾을 수 없는 경우, JobPostNotFoundException(404)")
    void update_WhenJobPostNotFound_ThrowJobPostNotFoundExceptionWith404() throws Exception {
        // JobPostNotFoundException 발생시킴
        when(jobPostService.update(any(JobPostUpdateRequest.class), anyLong()))
                .thenThrow(new JobPostNotFoundException());

        mockMvc.perform(put("/job/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobPostUpdateRequest)))
                .andExpect(status().isNotFound()) // 상태 코드 반환
                .andExpect(jsonPath("$.title").value("JobPostNotFoundException"));
    }

    @Test
    @DisplayName("채용 공고 목록: 검색어가 없는 경우, 전체 공고 반환")
    void getList_WhenNoSearchQuery_ShouldReturnEntireList() throws Exception {
        String searchQuery = null; // 검색어 없음
        when(jobPostService.getAll()).thenReturn(List.of(jobPostResponse));

        mockMvc.perform(get("/job"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].jobPostId").value(jobPostResponse.getJobPostId()))
                .andExpect(jsonPath("$[0].position").value(jobPostResponse.getPosition()));
    }

    @Test
    @DisplayName("채용 공고 목록: 검색어가 있는 경우, 검색된 공고 반환")
    void getList_WhenHasSearchQuery_ShouldReturnSearchedList() throws Exception {
        String searchQuery = "검색어"; // 검색어 있음!

        when(jobPostService.search(searchQuery)).thenReturn(List.of(jobPostResponse));

        mockMvc.perform(get("/job?search=" + searchQuery)) // request param
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].jobPostId").value(jobPostResponse.getJobPostId()))
                .andExpect(jsonPath("$[0].position").value(jobPostResponse.getPosition()));
    }

    @Test
    @DisplayName("채용 공고 목록: 검색어가 유효성 검사를 통과하지 못하는 경우")
    void getList_WhenInvalidSearchQuery_ShouldThrowInvalidSearchQueryException() throws Exception {
        String searchQuery = "원티드$"; // 검색어 유효성 검사 통과 X

        when(jobPostService.search(searchQuery)).thenThrow(InvalidSearchQueryException.class);

        mockMvc.perform(get("/job?search=" + searchQuery)) // request param
                .andExpect(status().isBadRequest()) // 상태 코드 반환
                .andExpect(jsonPath("$.title").value("InvalidSearchQueryException"));
    }

    @Test
    @DisplayName("채용 공고 상세: JobPostId가 유효한 경우, 상세 정보를 반환")
    void getDetail_WhenAllValid_ShouldReturnJobPostDetail() throws Exception {
        when(jobPostService.getDetail(anyLong())).thenReturn(jobPostDetail);

        mockMvc.perform(get("/job/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobPostId").value(jobPostDetail.getJobPostId()));
    }

    @Test
    @DisplayName("채용 공고 상세: 해당 JobPost가 없는 경우, JobPostNotFoundException(404)")
    void getDetail_WhenJobPostNotFound_ThrowJobPostNotFoundExceptionWith404() throws Exception {
        when(jobPostService.getDetail(anyLong())).thenThrow(new JobPostNotFoundException());

        mockMvc.perform(get("/job/325237"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("JobPostNotFoundException"));
    }

    @Test
    @DisplayName("공고 정보 삭제")
    void delete_WhenValid_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/job/1"))
                .andExpect(status().isNoContent());
    }
}
