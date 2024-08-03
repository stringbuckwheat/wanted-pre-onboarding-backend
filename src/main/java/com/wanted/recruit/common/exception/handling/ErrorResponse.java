package com.wanted.recruit.common.exception.handling;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 전역 예외처리 응답용 DTO
 */
@AllArgsConstructor
@Getter
public class ErrorResponse {
    private String title; // 발생한 exception 이름
    private String message; // 예외 메시지
}
