package com.wanted.recruit.exception;

public class JobPostNotFoundException extends RuntimeException {
    public JobPostNotFoundException() {
        super("해당 채용정보를 찾을 수 없어요");
    }
}
