package com.wanted.recruit.jobpost.controller;

import com.wanted.recruit.jobpost.dto.JobPostRequest;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class InvalidJobPostRequestProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(
                        JobPostRequest.builder()
                                .companyId(null) // invalid
                                .position("Valid Position")
                                .reward(500000)
                                .techStack("Java/Spring")
                                .content("Valid Content")
                                .build()
                ),
                Arguments.of(
                        JobPostRequest.builder()
                                .companyId(1L)
                                .position("")  // invalid
                                .reward(500000)
                                .techStack("Java/Spring")
                                .content("Valid Content")
                                .build()
                ),
                Arguments.of(
                        JobPostRequest.builder()
                                .companyId(1L)
                                .position("Valid Position")
                                .reward(-1)  // Invalid
                                .techStack("Java/Spring")
                                .content("Valid Content")
                                .build()
                ),
                Arguments.of(
                        JobPostRequest.builder()
                                .companyId(1L)
                                .position("Valid Position")
                                .reward(500000)
                                .techStack("")  // invalid
                                .content("Valid Content")
                                .build()
                ),
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
