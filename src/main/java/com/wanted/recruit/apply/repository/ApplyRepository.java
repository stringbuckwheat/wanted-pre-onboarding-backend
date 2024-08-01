package com.wanted.recruit.apply.repository;

import com.wanted.recruit.apply.Apply;
import org.springframework.data.repository.Repository;

public interface ApplyRepository extends Repository<Apply, Long>, ApplyQueryRepository {
    Apply save(Apply apply);
}
