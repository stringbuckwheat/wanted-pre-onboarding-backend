package com.wanted.recruit.jobpost.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.wanted.recruit.jobpost.entity.JobPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 채용 공고 상세 정보 응답용 DTO
 * {@link JobPost}의 기본 정보와 해당 회사의 다른 채용 공고 ID 목록을 포함
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class JobPostDetail extends JobPostResponse {
    // 해당 회사의 다른 채용 공고 ID 목록
    private List<Long> otherJotPostByCompany;

    @QueryProjection
    public JobPostDetail(JobPost jobPost, List<Long> otherJotPostByCompany) {
        super(jobPost);
        this.otherJotPostByCompany = otherJotPostByCompany;
    }
}
