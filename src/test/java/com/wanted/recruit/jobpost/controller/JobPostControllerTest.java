package com.wanted.recruit.jobpost.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.recruit.common.Company;
import com.wanted.recruit.exception.JobPostNotFoundException;
import com.wanted.recruit.jobpost.JobPost;
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
    @DisplayName("모든 값이 유효한 경우, 채용공고 저장(201)")
    void save_WhenValid_ShouldReturnSavedJobPostWith204() throws Exception {
        when(jobPostService.save(any(JobPostRequest.class))).thenReturn(jobPostResponse);

        mockMvc.perform(post("/job")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobPostRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.jobPostId").value(jobPostResponse.getJobPostId()))
                .andExpect(jsonPath("$.position").value(jobPostResponse.getPosition()));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidJobPostRequestProvider.class)
    @DisplayName("요청 데이터가 불충분한 경우, MethodArgumentNotValidException(400)")
    void save_WhenRequestDataInvalid_ShouldThrowMethodArgumentNotValidExceptionWith400(JobPostRequest invalidRequest) throws Exception {
        mockMvc.perform(post("/job")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("MethodArgumentNotValidException"));
        ;
    }

    @Test
    @DisplayName("모든 데이터가 유효한 경우, 공고 수정")
    void update_whenAllValid_ShouldReturnUpdatedJobPost() throws Exception {
        when(jobPostService.update(any(JobPostUpdateRequest.class), anyLong())).thenReturn(jobPostResponse);

        mockMvc.perform(put("/job/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobPostUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobPostId").value(jobPostResponse.getJobPostId()))
                .andExpect(jsonPath("$.position").value(jobPostResponse.getPosition()));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidJobPostUpdateRequestProvider.class)
    @DisplayName("요청 데이터가 불충분한 경우, MethodArgumentNotValidException(400)")
    void update_WhenRequestDataInvalid_ShouldThrowMethodArgumentNotValidExceptionWith400(JobPostUpdateRequest invalidRequest) throws Exception {
        mockMvc.perform(put("/job/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("MethodArgumentNotValidException"));
    }

    @Test
    @DisplayName("공고 정보를 찾을 수 없는 경우, JobPostNotFoundException(404)")
    void update_WhenJobPostNotFound_ThrowJobPostNotFoundExceptionWith404() throws Exception {
        Long nonExistJobPostId = 23947123L;

        when(jobPostService.update(any(JobPostUpdateRequest.class), eq(nonExistJobPostId)))
                .thenThrow(new JobPostNotFoundException());

        mockMvc.perform(put("/job/" + nonExistJobPostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobPostUpdateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("JobPostNotFoundException"));
    }

    @Test
    @DisplayName("검색어가 없는 경우, 전체 공고 반환")
    void getList_WhenNoSearchQuery_ShouldReturnEntireList() throws Exception {
        when(jobPostService.getList(null)).thenReturn(List.of(jobPostResponse));

        mockMvc.perform(get("/job"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].jobPostId").value(jobPostResponse.getJobPostId()))
                .andExpect(jsonPath("$[0].position").value(jobPostResponse.getPosition()));
    }

    @Test
    @DisplayName("검색어가 있는 경우, 검색된 공고 반환")
    void getList_WhenHasSearchQuery_ShouldReturnSearchedList() throws Exception {
        when(jobPostService.getList("검색어")).thenReturn(List.of(jobPostResponse));

        mockMvc.perform(get("/job?search=검색어"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].jobPostId").value(jobPostResponse.getJobPostId()))
                .andExpect(jsonPath("$[0].position").value(jobPostResponse.getPosition()));
    }

    @Test
    @DisplayName("JobPostId가 유효한 경우, 상세 정보를 반환")
    void getDetail_WhenAllValid_ShouldReturnJobPostDetail() throws Exception {
        when(jobPostService.getDetail(anyLong())).thenReturn(jobPostDetail);

        mockMvc.perform(get("/job/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobPostId").value(jobPostDetail.getJobPostId()));
    }

    @Test
    @DisplayName("JobPost가 없는 경우, JobPostNotFoundException(404)")
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
