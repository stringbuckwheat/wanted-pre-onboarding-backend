package com.wanted.recruit.jobpost.controller;

import com.wanted.recruit.jobpost.dto.JobPostRequest;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

/**
 * 유효하지 않은 채용공고 등록 요청 DTO{@link JobPostRequest}를 제공하는 클래스
 */
public class InvalidJobPostRequestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                // 1) companyId가 null인 경우
                Arguments.of(
                        JobPostRequest.builder()
                                .companyId(null) // invalid
                                .position("Valid Position")
                                .reward(500000)
                                .techStack("Java/Spring")
                                .content("Valid Content")
                                .build()
                ),

                // 2) position이 빈 문자열인 경우
                Arguments.of(
                        JobPostRequest.builder()
                                .companyId(1L)
                                .position("")  // invalid
                                .reward(500000)
                                .techStack("Java/Spring")
                                .content("Valid Content")
                                .build()
                ),

                // 3) 채용 보상금이 음수인 경우
                Arguments.of(
                        JobPostRequest.builder()
                                .companyId(1L)
                                .position("Valid Position")
                                .reward(-1)  // Invalid
                                .techStack("Java/Spring")
                                .content("Valid Content")
                                .build()
                ),

                // 4) 기술스택을 작성하지 않은 경우
                Arguments.of(
                        JobPostRequest.builder()
                                .companyId(1L)
                                .position("Valid Position")
                                .reward(500000)
                                .techStack("")  // invalid
                                .content("Valid Content")
                                .build()
                ),

                // 5) 채용공고 상세 내용을 작성하지 않은 경우
                Arguments.of(
                        JobPostRequest.builder()
                                .companyId(1L)
                                .position("Valid Position")
                                .reward(500000)
                                .techStack("Java/Spring")
                                .content("") // invalid
                                .build()
                )
        );
    }
}
