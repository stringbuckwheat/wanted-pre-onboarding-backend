package com.wanted.recruit.jobpost.controller;

import com.wanted.recruit.common.exception.exception.CompanyNotFoundException;
import com.wanted.recruit.common.exception.exception.JobPostNotFoundException;
import com.wanted.recruit.jobpost.dto.JobPostDetail;
import com.wanted.recruit.jobpost.dto.JobPostRequest;
import com.wanted.recruit.jobpost.dto.JobPostResponse;
import com.wanted.recruit.jobpost.dto.JobPostUpdateRequest;
import com.wanted.recruit.jobpost.service.JobPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 채용 공고 RESTful API 제공 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class JobPostController {
    private final JobPostService jobPostService;

    /**
     * 새로운 채용 공고 저장
     *
     * @param request 저장할 채용 공고의 요청 데이터
     * @return 저장된 채용 공고 응답 DTO
     * @throws MethodArgumentNotValidException 요청 데이터 유효성 검사 실패 시(BAD_REQUEST)
     * @throws CompanyNotFoundException        해당 회사 정보가 존재하지 않는 경우(NOT_FOUND)
     */
    @PostMapping("/job")
    public ResponseEntity<JobPostResponse> save(@RequestBody @Valid JobPostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobPostService.save(request));
    }

    /**
     * 기존 채용 공고 수정
     *
     * @param request 수정할 채용 공고의 요청 데이터
     * @param id      수정할 채용 공고 ID
     * @return 수정된 채용 공고 응답 DTO
     * @throws MethodArgumentNotValidException 요청 데이터 유효성 검사 실패 시(BAD_REQUEST)
     * @throws JobPostNotFoundException        해당 지원 공고가 존재하지 않는 경우(NOT_FOUND)
     */
    @PutMapping("/job/{id}")
    public ResponseEntity<JobPostResponse> update(@RequestBody @Valid JobPostUpdateRequest request, @PathVariable("id") Long id) {
        return ResponseEntity.ok().body(jobPostService.update(request, id));
    }

    /**
     * 채용 공고 목록 조회
     *
     * @param searchQuery (optional) 채용 공고 필터링 조건
     * @return 채용 공고 목록
     */
    @GetMapping("/job")
    public ResponseEntity<List<JobPostResponse>> getAll(@RequestParam(value = "search", required = false) String searchQuery) {
        return ResponseEntity.ok().body(jobPostService.getList(searchQuery));
    }

    /**
     * 특정 채용 공고의 세부 정보 조회
     *
     * @param id 조회할 채용 공고 Id
     * @return 채용 공고 세부정보
     * @throws JobPostNotFoundException 해당 지원 공고가 존재하지 않는 경우(NOT_FOUND)
     */
    @GetMapping("/job/{id}")
    public ResponseEntity<JobPostDetail> getDetail(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(jobPostService.getDetail(id));
    }

    /**
     * 특정 채용 공고 삭제
     *
     * @param id 삭제할 공고 id
     * @throws JobPostNotFoundException 해당 지원 공고가 존재하지 않는 경우(NOT_FOUND)
     */
    @DeleteMapping("/job/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        jobPostService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
