package com.wanted.recruit.jobpost.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wanted.recruit.jobpost.JobPost;
import com.wanted.recruit.jobpost.dto.JobPostResponse;
import com.wanted.recruit.jobpost.dto.QJobPostResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.wanted.recruit.common.QCompany.company;
import static com.wanted.recruit.jobpost.QJobPost.jobPost;

@RequiredArgsConstructor
public class JobPostQueryRepositoryImpl implements JobPostQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<JobPostResponse> search(String searchQuery) {
        return queryFactory.select(
                        new QJobPostResponse(jobPost)
                )
                .from(jobPost)
                .join(jobPost.company, company)
                .where(
                        jobPost.company.name.contains(searchQuery)
                                .or(jobPost.company.nation.contains(searchQuery))
                                .or(jobPost.company.region.contains(searchQuery))
                                .or(jobPost.content.contains(searchQuery))
                                .or(jobPost.position.contains(searchQuery))
                                .or(jobPost.techStack.contains(searchQuery))
                )
                .fetch();
    }

    public List<Long> getOtherJobPost(Long companyId, Long jobPostId) {
        return queryFactory
                .select(jobPost.id)
                .from(jobPost)
                .where(
                        jobPost.company.id.eq(companyId)
                                .and(jobPost.id.ne(jobPostId))
                )
                .fetch();
    }
}
