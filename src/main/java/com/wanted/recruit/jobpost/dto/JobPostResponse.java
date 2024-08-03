package com.wanted.recruit.jobpost.dto;


import com.querydsl.core.annotations.QueryProjection;
import com.wanted.recruit.jobpost.entity.JobPost;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 채용 공고 생성 응답용 DTO
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostResponse {
    private Long jobPostId; // 채용 공고 id
    private String position; // 채용 포지션
    private int reward; // 채용 보상금
    private String content; // 채용 내용
    private String techStack; // 사용 기술
    private LocalDateTime createDate; // 생성일
    private LocalDateTime updateDate; // 수정일
    private CompanyResponse company; // 채용 공고를 작성한 회사

    @QueryProjection
    public JobPostResponse(JobPost jobPost) {
        this.jobPostId = jobPost.getId();
        this.position = jobPost.getPosition();
        this.reward = jobPost.getReward();
        this.content = jobPost.getContent();
        this.techStack = jobPost.getTechStack();
        this.createDate = jobPost.getCreatedDate();
        this.updateDate = jobPost.getUpdatedDate();
        this.company = new CompanyResponse(jobPost.getCompany());
    }
}
