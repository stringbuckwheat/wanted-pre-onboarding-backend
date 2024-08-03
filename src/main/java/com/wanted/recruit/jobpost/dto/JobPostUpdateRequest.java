package com.wanted.recruit.jobpost.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 채용 공고 수정 요청용 DTO
 */
@Getter
@Builder // 테스트용
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JobPostUpdateRequest {
    @NotBlank(message = "채용 포지션을 입력해주세요.")
    private String position; // 채용 포지션

    @Min(value = 0, message = "채용 보상금은 0원 이상이어야 합니다.")
    private int reward; // 채용 보상금

    @NotBlank(message = "채용 내용을 입력해주세요.")
    private String content; // 채용 내용

    @NotBlank(message = "사용 기술을 입력해주세요.")
    private String techStack; // 사용 기술
}
