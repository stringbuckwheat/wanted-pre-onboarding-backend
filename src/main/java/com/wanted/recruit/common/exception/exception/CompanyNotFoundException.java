package com.wanted.recruit.common.exception.exception;

/**
 * 회사를 찾을 수 없는 경우의 예외
 */
public class CompanyNotFoundException extends RuntimeException {
    public CompanyNotFoundException() {
        super("해당 회사를 찾을 수 없어요.");
    }
}
