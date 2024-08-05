package com.wanted.recruit.jobpost.repository;

import com.wanted.recruit.company.entity.Company;
import com.wanted.recruit.jobpost.entity.JobPost;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JobPostTestHelper {
    public static void setUpJobPostTestData(final String TEST_PREFIX, TestEntityManager entityManager) {
        Company wanted = createCompany(TEST_PREFIX, "원티드", "한국", "서울");
        Company naver = createCompany(TEST_PREFIX, "네이버", "한국", "판교");
        Company nintendo = createCompany(TEST_PREFIX, "닌텐도", "일본", "교토");

        entityManager.persist(wanted);
        entityManager.persist(naver);
        entityManager.persist(nintendo);

        List<JobPost> jobPosts = List.of(
                createJobPost(TEST_PREFIX, wanted, "신입 백엔드", "Java", "신입 개발자 모집"),
                createJobPost(TEST_PREFIX, naver, "경력 백엔드", "TypeScript", "경력 2년 이상"),
                createJobPost(TEST_PREFIX, nintendo, "백엔드", "Python/Django, Java/Spring", "경력 우대"),
                createJobPost(TEST_PREFIX, wanted, "신입 프론트엔드", "React", "채용 공고 내용"),
                createJobPost(TEST_PREFIX, naver, "경력 프론트엔드", "Angular", "백엔드와 협업 능력을..."),
                createJobPost(TEST_PREFIX, wanted, "프론트엔드", "Vue.js, React.js", "채용 공고 내용")
        );

        jobPosts.forEach(entityManager::persist);
    }

    public static String generateUniqueString(final String TEST_PREFIX, String base) {
        return TEST_PREFIX + base;
    }

    public static String generateUniqueString(final String TEST_PREFIX, String regex, String base) {
        // 문자열을 구분자로 나누기
        String[] parts = base.split(regex);

        // 각 부분에 접두사 추가
        String prefixedParts = Arrays.stream(parts)
                .map(part -> TEST_PREFIX + part) // 접두사 추가
                .collect(Collectors.joining(", ")); // 다시 조합

        return prefixedParts;
    }

    public static Company createCompany(final String TEST_PREFIX, String name, String nation, String region) {
        return new Company(generateUniqueString(TEST_PREFIX, name), generateUniqueString(TEST_PREFIX, nation), generateUniqueString(TEST_PREFIX, region));
    }

    public static JobPost createJobPost(final String TEST_PREFIX, Company company, String position, String techStack, String content) {
        return JobPost.builder()
                .reward(30 * 100000)
                .position(generateUniqueString(TEST_PREFIX, " ", position))
                .techStack(generateUniqueString(TEST_PREFIX, ", ", techStack))
                .content(generateUniqueString(TEST_PREFIX, " ", content))
                .company(company)
                .build();
    }
}
