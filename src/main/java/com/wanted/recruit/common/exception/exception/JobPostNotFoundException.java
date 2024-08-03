package com.wanted.recruit.common.exception.exception;

/**
 * 채용 공고를 찾을 수 없는 경우
 */
public class JobPostNotFoundException extends RuntimeException {
    public JobPostNotFoundException() {
        super("해당 채용 공고를 찾을 수 없어요");
    }
}
