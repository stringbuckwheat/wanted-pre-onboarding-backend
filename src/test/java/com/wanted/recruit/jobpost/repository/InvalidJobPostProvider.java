package com.wanted.recruit.jobpost.repository;

import com.wanted.recruit.jobpost.entity.JobPost;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

/**
 * 유효하지 않은 JobPostBuilder를 제공하는 클래스
 * 실제 검사 시 영속화된 Company 엔티티를 추가하여 build
 */
public class InvalidJobPostProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                // 1) 채용 포지션을 입력하지 않은 경우
                Arguments.of(
                        JobPost.builder()
                                .position("") // invalid
                                .reward(500000)
                                .techStack("Java/Spring")
                                .content("공고 상세 내용")
                ),

                // 2) 채용 보상금이 음수인 경우
                Arguments.of(
                        JobPost.builder()
                                .position("신입 백엔드 개발자")
                                .reward(-1) // invalid
                                .techStack("Java/Spring")
                                .content("공고 상세 내용")
                ),

                // 3) 기술 스택을 명시하지 않은 경우
                Arguments.of(
                        JobPost.builder()
                                .position("신입 백엔드 개발자")
                                .reward(500000)
                                .techStack("") // invalid
                                .content("공고 상세 내용")
                ),

                // 4) 채용 상세를 적지 않은 경우
                Arguments.of(
                        JobPost.builder()
                                .position("신입 백엔드 개발자")
                                .reward(500000)
                                .techStack("Java/Spring")
                                .content("") // invalid
                )
        );
    }
}
