package com.wanted.recruit.common.exception.exception;

public class InvalidSearchQueryException extends RuntimeException {
    public InvalidSearchQueryException() {
        super("검색어로는 알파벳, 한글, 숫자, 공백, 하이픈(-), 언더스코어(_), 콤마(,), 슬래시(/), 괄호((), {}, [])만 사용할 수 있어요.");
    }
}