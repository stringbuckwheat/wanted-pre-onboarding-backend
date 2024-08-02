package com.wanted.recruit.jobpost.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.wanted.recruit.jobpost.JobPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class JobPostDetail extends JobPostResponse {
    private List<Long> otherJotPostByCompany;

    @QueryProjection
    public JobPostDetail(JobPost jobPost, List<Long> otherJotPostByCompany) {
        super(jobPost);
        this.otherJotPostByCompany = otherJotPostByCompany;
    }
}
