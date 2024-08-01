package com.wanted.recruit.exception.handling;

import com.wanted.recruit.exception.AlreadyAppliedException;
import com.wanted.recruit.exception.CompanyNotFoundException;
import com.wanted.recruit.exception.JobPostNotFoundException;
import com.wanted.recruit.exception.UserNotFoundException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public HttpEntity<ErrorResponse> handleException(MethodArgumentNotValidException e) {
        String title = e.getClass().getSimpleName();

        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError == null ? "요청 데이터를 확인해주세요" : fieldError.getDefaultMessage();
        // String title = fieldError.getField().toUpperCase() + "_" + fieldError.getCode().toUpperCase();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(title, message));
    }

    @ExceptionHandler(AlreadyAppliedException.class)
    public HttpEntity<ErrorResponse> handleException(AlreadyAppliedException e) {
        ErrorResponse er = new ErrorResponse(e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(er);
    }

    @ExceptionHandler(CompanyNotFoundException.class)
    public HttpEntity<ErrorResponse> handleException(CompanyNotFoundException e) {
        ErrorResponse er = new ErrorResponse(e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(er);
    }

    @ExceptionHandler(JobPostNotFoundException.class)
    public HttpEntity<ErrorResponse> handleException(JobPostNotFoundException e) {
        ErrorResponse er = new ErrorResponse(e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(er);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public HttpEntity<ErrorResponse> handleException(UserNotFoundException e) {
        ErrorResponse er = new ErrorResponse(e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(er);
    }
}
