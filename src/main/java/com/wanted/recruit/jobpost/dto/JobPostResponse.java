package com.wanted.recruit.jobpost.dto;


import com.querydsl.core.annotations.QueryProjection;
import com.wanted.recruit.jobpost.JobPost;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
public class JobPostResponse {
    private Long jobPostId;
    private String position; // 채용 포지션
    private int reward; // 채용 보상금
    private String content; // 채용 내용
    private String techStack; // 사용 기술
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private CompanyResponse company;

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
