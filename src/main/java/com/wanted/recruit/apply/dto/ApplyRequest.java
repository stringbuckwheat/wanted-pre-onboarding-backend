package com.wanted.recruit.apply.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 채용 공고 지원 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApplyRequest {
    // 사용자 pk
    @NotNull(message = "회원 정보를 입력해주세요")
    @Min(value = 0, message = "회원 정보를 확인해주세요")
    private Long userId;

    // 채용 공고 pk
    @NotNull(message = "채용 정보를 입력해주세요")
    @Min(value = 0, message = "채용 정보를 확인해주세요")
    private Long jobPostId;
}
