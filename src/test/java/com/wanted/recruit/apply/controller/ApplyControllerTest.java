package com.wanted.recruit.apply.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.recruit.apply.dto.ApplyRequest;
import com.wanted.recruit.apply.dto.ApplyResponse;
import com.wanted.recruit.apply.service.ApplyService;
import com.wanted.recruit.common.exception.exception.AlreadyAppliedException;
import com.wanted.recruit.common.exception.exception.JobPostNotFoundException;
import com.wanted.recruit.common.exception.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link ApplyController}계층 테스트
 */
@WebMvcTest(ApplyController.class)
@DisplayName("ApplyController 테스트")
class ApplyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplyService applyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("지원하기: 모든 값이 유효한 경우, 지원 내역 저장(201)")
    void apply_WhenValid_ShouldSaveApplyAndReturn201() throws Exception {
        // 요청 데이터
        ApplyRequest request = new ApplyRequest(1L, 2L);

        // 응답 데이터
        ApplyResponse response = ApplyResponse.builder()
                .userId(1L)
                .name("유저1")
                .jobPostId(2L)
                .position("신입 백엔드 개발자")
                .createDate(LocalDateTime.now())
                .build();

        when(applyService.apply(any(ApplyRequest.class))).thenReturn(response);

        mockMvc.perform(post("/job/2/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()) // 응답 상태 코드 확인
                .andExpect(jsonPath("$.userId").value(response.getUserId())) // 필드 값 검증
                .andExpect(jsonPath("$.jobPostId").value(request.getJobPostId())); // 필드 값 검증
    }

    static Stream<Arguments> provideInsufficientRequest() {
        return Stream.of(
                Arguments.of(new ApplyRequest(null, 2L)), // userId 없음
                Arguments.of(new ApplyRequest(1L, null)) // jobId 없음
        );
    }

    /**
     * 요청 데이터가 불충분할 때, MethodArgumentNotValidException을 발생시키고 400 Bad Request 응답을 반환하는지 테스트
     *
     * @param insufficientRequest userId나 jobId가 없는 요청 DTO
     */
    @ParameterizedTest
    @MethodSource("provideInsufficientRequest")
    @DisplayName("지원하기: 요청 데이터가 불충분한 경우, MethodArgumentNotValidException(400)")
    void apply_WhenRequestDataInvalid_ShouldThrowMethodArgumentNotValidExceptionWith400(ApplyRequest insufficientRequest) throws Exception {
        mockMvc.perform(post("/job/2/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insufficientRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("MethodArgumentNotValidException"));
    }

    @Test
    @DisplayName("지원하기: 이미 지원한 경우, JobPostNotFoundException(409)")
    void apply_WhenJobPostNotFound_ThrowAlreadyAppliedExceptionWith404() throws Exception {
        ApplyRequest request = new ApplyRequest(1L, 2L); // 요청 데이터 자체는 유효

        // 이미 지원 내역이 있음
        when(applyService.apply(any(ApplyRequest.class))).thenThrow(new AlreadyAppliedException());

        mockMvc.perform(post("/job/2/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict()) // 상태 코드 검증
                .andExpect(jsonPath("$.title").value("AlreadyAppliedException")); // 필드값 검증
    }

    @Test
    @DisplayName("지원하기: 공고 정보를 찾을 수 없는 경우, JobPostNotFoundException(404)")
    void apply_WhenJobPostNotFound_ThrowJobPostNotFoundExceptionWith404() throws Exception {
        Long nonExistJobPostId = 23947123L; // 존재하지 않는 JobPostId
        ApplyRequest invalidRequest = new ApplyRequest(1L, nonExistJobPostId); // 존재하지 않는 JobPostId를 포함하는 요청 DTO

        // JobPostNotFoundException 발생 시킴
        when(applyService.apply(any(ApplyRequest.class))).thenThrow(new JobPostNotFoundException());

        mockMvc.perform(post("/job/" + nonExistJobPostId + "/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isNotFound()) // 응답 코드
                .andExpect(jsonPath("$.title").value("JobPostNotFoundException")); // 필드값
    }

    @Test
    @DisplayName("지원하기: 사용자 정보를 찾을 수 없는 경우, UserNotFoundException(404)")
    void apply_WhenUserNotFound_ThrowUserNotFoundExceptionWith404() throws Exception {
        Long nonExistUserId = 23947123L; // 존재하지 않는 userId
        ApplyRequest invalidRequest = new ApplyRequest(nonExistUserId, 2L); // 존재하지 않는 userId를 포함하는 요청 DTO

        // UserNotFoundException 발생
        when(applyService.apply(any(ApplyRequest.class))).thenThrow(new UserNotFoundException());

        mockMvc.perform(post("/job/2/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isNotFound()) // 응답 코드
                .andExpect(jsonPath("$.title").value("UserNotFoundException")); // 필드값 검사
    }
}