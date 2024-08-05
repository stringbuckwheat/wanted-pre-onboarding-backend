package com.wanted.recruit.jobpost.controller;

import com.wanted.recruit.jobpost.dto.JobPostUpdateRequest;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

/**
 * 유효하지 않은 채용공고 수정 요청 DTO{@link JobPostUpdateRequest}를 제공하는 클래스
 */
public class InvalidJobPostUpdateRequestProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                // 1) 포지션을 작성하지 않은 경우
                Arguments.of(
                        JobPostUpdateRequest.builder()
                                .position("") // invalid
                                .reward(50 * 100000)
                                .content("채용 내용")
                                .techStack("Java / Spring")
                                .build()
                ),

                // 2) 채용 보상금이 음수인 경우
                Arguments.of(
                        JobPostUpdateRequest.builder()
                                .position("채용 포지션")
                                .reward(-1) // invalid
                                .content("채용 내용")
                                .techStack("Java / Spring")
                                .build()
                ),

                // 3) 채용 상세 내용이 없는 경우
                Arguments.of(
                        JobPostUpdateRequest.builder()
                                .position("채용 포지션")
                                .reward(50 * 100000)
                                // no content
                                .techStack("Java / Spring")
                                .build()
                ),

                // 4) 기술 스택을 작성하지 않은 경우
                Arguments.of(
                        JobPostUpdateRequest.builder()
                                .position("채용 포지션")
                                .reward(-1)
                                .content("채용 내용")
                                .techStack(null) // invalid
                                .build()
                )
        );
    }
}
