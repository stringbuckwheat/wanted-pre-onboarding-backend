package com.wanted.recruit.apply.repository;

import com.wanted.recruit.apply.entity.Apply;
import org.springframework.data.repository.Repository;

/**
 *  Apply 엔티티의 Spring Data JPA Repository 인터페이스
 */
public interface ApplyRepository extends Repository<Apply, Long>, ApplyQueryRepository {
    Apply save(Apply apply);
}
