package com.wanted.recruit.common.exception.exception;

/**
 * 사용자가 이미 지원한 공고에 대해 다시 지원하려 할 때 발생
 */
public class AlreadyAppliedException extends RuntimeException {
    public AlreadyAppliedException() {
        super("이미 지원한 공고예요.");
    }
}
