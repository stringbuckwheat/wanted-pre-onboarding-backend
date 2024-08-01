package com.wanted.recruit.exception;

public class AlreadyAppliedException extends RuntimeException {
    public AlreadyAppliedException() {
        super("이미 지원한 채용공고 입니다.");
    }
}
