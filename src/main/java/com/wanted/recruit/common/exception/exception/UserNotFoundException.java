package com.wanted.recruit.common.exception.exception;

/**
 * 사용자를 찾을 수 없는 경우
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("해당 사용자를 찾을 수 없어요");
    }
}
