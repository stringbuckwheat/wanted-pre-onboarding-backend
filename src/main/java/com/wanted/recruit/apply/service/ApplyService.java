package com.wanted.recruit.apply.service;

import com.wanted.recruit.apply.dto.ApplyRequest;
import com.wanted.recruit.apply.dto.ApplyResponse;
import org.springframework.transaction.annotation.Transactional;

/**
 * 채용 지원 서비스 인터페이스
 */
public interface ApplyService {

    /**
     * 지원 요청 처리
     * 사용자가 특정 채용 공고에 지원
     * @param request 지원 요청 정보
     * @return 지원 결과 및 응답 객체
     */
    @Transactional
    ApplyResponse apply(ApplyRequest request);
}
