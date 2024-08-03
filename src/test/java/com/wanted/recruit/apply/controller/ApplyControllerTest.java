package com.wanted.recruit.apply.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.recruit.apply.dto.ApplyRequest;
import com.wanted.recruit.apply.dto.ApplyResponse;
import com.wanted.recruit.apply.service.ApplyService;
import com.wanted.recruit.exception.AlreadyAppliedException;
import com.wanted.recruit.exception.JobPostNotFoundException;
import com.wanted.recruit.exception.UserNotFoundException;
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

@WebMvcTest(ApplyController.class)
class ApplyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplyService applyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("모든 값이 유효한 경우, 지원 내역 저장(201)")
    void apply_WhenValid_ShouldSaveApplyAndReturn201() throws Exception {
        ApplyRequest request = new ApplyRequest(1L, 2L);

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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(response.getUserId()))
                .andExpect(jsonPath("$.jobPostId").value(request.getJobPostId()));
    }

    static Stream<Arguments> provideInsufficientRequest() {
        return Stream.of(
                Arguments.of(new ApplyRequest(null, 2L)),
                Arguments.of(new ApplyRequest(1L, null))
        );
    }

    @ParameterizedTest
    @MethodSource("provideInsufficientRequest")
    @DisplayName("요청 데이터가 불충분한 경우, MethodArgumentNotValidException(400)")
    void apply_WhenRequestDataInvalid_ShouldThrowMethodArgumentNotValidExceptionWith400(ApplyRequest insufficientRequest) throws Exception {
        // userId 필드가 없거나, jobPost 필드가 없거나
        mockMvc.perform(post("/job/2/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insufficientRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("MethodArgumentNotValidException"));
    }

    @Test
    @DisplayName("이미 지원한 경우 경우, JobPostNotFoundException(409)")
    void apply_WhenJobPostNotFound_ThrowAlreadyAppliedExceptionWith404() throws Exception {
        ApplyRequest request = new ApplyRequest(1L, 2L);

        when(applyService.apply(any(ApplyRequest.class))).thenThrow(new AlreadyAppliedException());

        mockMvc.perform(post("/job/2/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("AlreadyAppliedException"));
    }

    @Test
    @DisplayName("공고 정보를 찾을 수 없는 경우, JobPostNotFoundException(404)")
    void apply_WhenJobPostNotFound_ThrowJobPostNotFoundExceptionWith404() throws Exception {
        Long nonExistJobPostId = 23947123L;
        ApplyRequest invalidRequest = new ApplyRequest(1L, nonExistJobPostId);

        when(applyService.apply(any(ApplyRequest.class))).thenThrow(new JobPostNotFoundException());

        mockMvc.perform(post("/job/" + nonExistJobPostId + "/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("JobPostNotFoundException"));
    }

    @Test
    @DisplayName("사용자 정보를 찾을 수 없는 경우, UserNotFoundException(404)")
    void apply_WhenUserNotFound_ThrowUserNotFoundExceptionWith404() throws Exception {
        Long nonExistUserId = 23947123L;
        ApplyRequest invalidRequest = new ApplyRequest(nonExistUserId, 2L);

        when(applyService.apply(any(ApplyRequest.class))).thenThrow(new UserNotFoundException());

        mockMvc.perform(post("/job/2/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("UserNotFoundException"));
    }

}