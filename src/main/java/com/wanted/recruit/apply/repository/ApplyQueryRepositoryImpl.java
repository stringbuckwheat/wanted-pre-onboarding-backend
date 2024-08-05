package com.wanted.recruit.apply.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.wanted.recruit.apply.entity.QApply.apply;


/**
 * {@link ApplyQueryRepository} 인터페이스 구현체
 * QueryDSL 사용
 */
@RequiredArgsConstructor
public class ApplyQueryRepositoryImpl implements ApplyQueryRepository {
    public final JPAQueryFactory queryFactory;

    /**
     * 채용공고 지원 여부 확인
     *
     * @param userId  사용자 ID
     * @param jobPostId  채용 공고 ID
     * @return 이미 지원했을 시 true, 아니면 false
     */
    public boolean existsByUserIdAndJobPostId(Long userId, Long jobPostId) {
        long count = queryFactory
                .selectFrom(apply)
                .where(apply.user.id.eq(userId)
                        .and(apply.jobPost.id.eq(jobPostId)))
                .fetch()
                .size();

        return count > 0;
    }
}
