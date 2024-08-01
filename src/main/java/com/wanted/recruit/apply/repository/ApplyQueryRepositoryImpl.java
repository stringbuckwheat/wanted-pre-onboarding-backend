package com.wanted.recruit.apply.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.wanted.recruit.apply.QApply.apply;


@RequiredArgsConstructor
public class ApplyQueryRepositoryImpl implements ApplyQueryRepository {
    public final JPAQueryFactory queryFactory;

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
