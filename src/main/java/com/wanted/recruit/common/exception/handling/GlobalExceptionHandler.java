package com.wanted.recruit.common.exception.handling;

import com.wanted.recruit.common.exception.exception.AlreadyAppliedException;
import com.wanted.recruit.common.exception.exception.CompanyNotFoundException;
import com.wanted.recruit.common.exception.exception.JobPostNotFoundException;
import com.wanted.recruit.common.exception.exception.UserNotFoundException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 클래스
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 요청 데이터의 유효성 검사 오류 처리
     * 필드 오류가 있을 경우 해당 오류 메시지를, 없을 경우 기본 메시지 반환
     * @param e MethodArgumentNotValidException
     * @return 400 BAD_REQUEST
     * {
     *     title: MethodArgumentNotValidException,
     *     message: 회원 정보를 입력해주세요
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public HttpEntity<ErrorResponse> handleException(MethodArgumentNotValidException e) {
        String title = e.getClass().getSimpleName();

        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError == null ? "요청 데이터를 확인해주세요" : fieldError.getDefaultMessage();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(title, message));
    }

    /**
     * 이미 지원한 공고에 다시 지원하려는 경우에 발생
     * @param e AlreadyAppliedException
     * @return 409 CONFLICT
     * {
     *     title: AlreadyAppliedException,
     *     message: 이미 지원한 공고예요
     * }
     */
    @ExceptionHandler(AlreadyAppliedException.class)
    public HttpEntity<ErrorResponse> handleException(AlreadyAppliedException e) {
        ErrorResponse er = new ErrorResponse(e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(er);
    }

    /**
     * 해당 company_id의 회사 정보를 찾을 수 없는 경우
     * @param e CompanyNotFoundException
     * @return 404 NOT_FOUND
     * {
     *     title: CompanyNotFoundException,
     *     message: 해당 회사를 찾을 수 없어요
     * }
     */
    @ExceptionHandler(CompanyNotFoundException.class)
    public HttpEntity<ErrorResponse> handleException(CompanyNotFoundException e) {
        ErrorResponse er = new ErrorResponse(e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(er);
    }

    /**
     * 해당 job_post_id의 채용 공고를 찾을 수 없는 경우
     * @param e JobPostNotFoundException
     * @return 404 NOT_FOUND
     * {
     *     title: JobPostNotFoundException,
     *     message: 해당 채용 공고를 찾을 수 없어요
     * }
     */
    @ExceptionHandler(JobPostNotFoundException.class)
    public HttpEntity<ErrorResponse> handleException(JobPostNotFoundException e) {
        ErrorResponse er = new ErrorResponse(e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(er);
    }

    /**
     * 해당 user_id의 회원 정보를 찾을 수 없는 경우
     * @param e UserNotFoundException
     * @return 404 NOT_FOUND
     * {
     *     title: UserNotFoundException,
     *     message: 해당 사용자를 찾을 수 없어요
     * }
     */
    @ExceptionHandler(UserNotFoundException.class)
    public HttpEntity<ErrorResponse> handleException(UserNotFoundException e) {
        ErrorResponse er = new ErrorResponse(e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(er);
    }
}
