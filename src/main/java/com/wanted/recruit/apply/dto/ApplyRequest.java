package com.wanted.recruit.apply.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApplyRequest {
    @NotNull(message = "회원 정보를 입력해주세요")
    @Min(value = 0, message = "회원 정보를 확인해주세요")
    private Long userId;

    @NotNull(message = "채용 정보를 입력해주세요")
    @Min(value = 0, message = "채용 정보를 확인해주세요")
    private Long jobPostId;
}
