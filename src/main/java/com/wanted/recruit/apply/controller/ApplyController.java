package com.wanted.recruit.apply.controller;

import com.wanted.recruit.apply.dto.ApplyRequest;
import com.wanted.recruit.apply.dto.ApplyResponse;
import com.wanted.recruit.apply.service.ApplyService;
import com.wanted.recruit.common.exception.exception.AlreadyAppliedException;
import com.wanted.recruit.common.exception.exception.JobPostNotFoundException;
import com.wanted.recruit.common.exception.exception.UserNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 채용 공고 지원 요청 처리 컨트롤러
 */
@RestController
@RequiredArgsConstructor
public class ApplyController {
    private final ApplyService applyService;

    /**
     * 채용 공고 지원 요청 처리 엔드포인트
     * @param request 채용 공고 지원 요청 데이터, 유효성 검사 O
     * @return 채용 공고 지원 응답, 201 상태 코드
     * @throws MethodArgumentNotValidException 요청 데이터 유효성 검사 실패 시(BAD_REQUEST)
     * @throws AlreadyAppliedException 이미 지원한 공고인 경우(CONFLICT)
     * @throws JobPostNotFoundException 해당 지원 공고가 존재하지 않는 경우(NOT_FOUND)
     * @throws UserNotFoundException 해당 사용자가 존재하지 않는 경우(NOT_FOUND)
     */
    @PostMapping("/job/{id}/apply")
    public ResponseEntity<ApplyResponse> apply(@RequestBody @Valid ApplyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(applyService.apply(request));
    }
}
