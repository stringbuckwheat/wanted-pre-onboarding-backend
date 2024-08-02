package com.wanted.recruit.jobpost.controller;

import com.wanted.recruit.jobpost.dto.JobPostUpdateRequest;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class InvalidJobPostUpdateRequestProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(
                        JobPostUpdateRequest.builder()
                                .position("") // invalid
                                .reward(50 * 100000)
                                .content("채용 내용")
                                .techStack("Java / Spring")
                                .build()
                ),
                Arguments.of(
                        JobPostUpdateRequest.builder()
                                .position("채용 포지션")
                                .reward(-1) // invalid
                                .content("채용 내용")
                                .techStack("Java / Spring")
                                .build()
                ),
                Arguments.of(
                        JobPostUpdateRequest.builder()
                                .position("채용 포지션")
                                .reward(50 * 100000)
                                // no content
                                .techStack("Java / Spring")
                                .build()
                ),
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
