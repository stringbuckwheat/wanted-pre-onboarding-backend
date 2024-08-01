package com.wanted.recruit.jobpost.dto;

import com.wanted.recruit.common.Company;
import com.wanted.recruit.jobpost.JobPost;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder // 테스트용
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JobPostRequest {
    @NotNull(message = "회사 정보를 입력해주세요.")
    private Long companyId;

    @NotBlank(message = "채용 포지션을 입력해주세요.")
    private String position; // 채용 포지션

    @Min(value = 0, message = "채용 보상금은 0원 이상이어야 합니다.")
    private int reward; // 채용 보상금

    @NotBlank(message = "채용 내용을 입력해주세요.")
    private String content; // 채용 내용

    @NotBlank(message = "사용 기술을 입력해주세요.")
    private String techStack; // 사용 기술

    public JobPost toEntity(Company company) {
        return JobPost.builder()
                .position(position)
                .reward(reward)
                .content(content)
                .techStack(techStack)
                .company(company)
                .build();
    }
}
