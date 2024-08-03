package com.wanted.recruit.jobpost.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wanted.recruit.jobpost.dto.JobPostResponse;
import com.wanted.recruit.jobpost.dto.QJobPostResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.wanted.recruit.company.entity.QCompany.company;
import static com.wanted.recruit.jobpost.entity.QJobPost.jobPost;

/**
 * {@link JobPostQueryRepository}의 구현체
 * QueryDSL 사용
 */
@RequiredArgsConstructor
public class JobPostQueryRepositoryImpl implements JobPostQueryRepository {
    private final JPAQueryFactory queryFactory;

    /**
     * 검색어를 통해 채용 공고 조회
     *
     * @param searchQuery 검색어
     * @return 검색어가 포함된 채용 공고 리스트
     */
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

    /**
     * 특정 회사의 다른 채용 공고 id 목록 조회
     * 특정 채용 공고(a)의 추가적인 정보로 제공 -> 즉, 채용정보 a는 제외
     *
     * @param companyId 회사 Id
     * @param jobPostId 채용 공고 id
     * @return 회사의 다른 채용 공고 ID 목록
     */
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
