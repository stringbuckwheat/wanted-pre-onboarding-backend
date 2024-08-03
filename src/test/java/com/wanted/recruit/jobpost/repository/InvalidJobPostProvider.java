package com.wanted.recruit.jobpost.repository;

import com.wanted.recruit.common.Company;
import com.wanted.recruit.jobpost.JobPost;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class InvalidJobPostProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        Company company = Company.builder().name("원티드").nation("한국").region("서울").build();

        return Stream.of(
                Arguments.of(
                        JobPost.builder()
                                .position("") // invalid
                                .reward(500000)
                                .techStack("Java/Spring")
                                .content("공고 상세 내용")
                                .company(company)
                ),
                Arguments.of(
                        JobPost.builder()
                                .position("신입 백엔드 개발자")
                                .reward(-1) // invalid
                                .techStack("Java/Spring")
                                .content("공고 상세 내용")
                                .company(company)
                ),
                Arguments.of(
                        JobPost.builder()
                                .position("신입 백엔드 개발자")
                                .reward(500000)
                                .techStack("") // invalid
                                .content("공고 상세 내용")
                                .company(company)
                ),
                Arguments.of(
                        JobPost.builder()
                                .position("신입 백엔드 개발자")
                                .reward(500000)
                                .techStack("Java/Spring")
                                .content("") // invalid
                                .company(company)
                )
        );
    }
}
