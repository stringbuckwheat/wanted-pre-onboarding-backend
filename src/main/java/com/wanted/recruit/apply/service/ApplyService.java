package com.wanted.recruit.apply.service;

import com.wanted.recruit.apply.dto.ApplyRequest;
import com.wanted.recruit.apply.dto.ApplyResponse;
import org.springframework.transaction.annotation.Transactional;

public interface ApplyService {
    @Transactional
    ApplyResponse apply(ApplyRequest request);
}
