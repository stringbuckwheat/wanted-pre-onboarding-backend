package com.wanted.recruit.jobpost.repository;

import com.wanted.recruit.config.JpaConfig;
import com.wanted.recruit.config.QueryDslConfig;
import com.wanted.recruit.jobpost.JobPost;
import com.wanted.recruit.common.Company;
import com.wanted.recruit.jobpost.dto.JobPostResponse;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QueryDslConfig.class, JpaConfig.class})
class JobPostRepositoryTest {

    @Autowired
    private JobPostRepository jobPostRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Company company;
    private JobPost jobPost;

    @BeforeEach
    void setUp() {
        company = Company.builder().name("원티드").nation("한국").region("서울").build();
        entityManager.persist(company);

        jobPost = JobPost.builder()
                .position("신입 백엔드 개발자")
                .reward(500000)
                .techStack("Java/Spring")
                .content("공고 상세 내용")
                .company(company)
                .build();
    }


    @Test
    @DisplayName("JobPost 저장 테스트")
    void save_whenValid_ShouldSaveJobPost() {
        JobPost savedJobPost = jobPostRepository.save(jobPost);

        assertThat(savedJobPost.getId()).isNotNull();
        assertThat(savedJobPost.getCompany().getName()).isEqualTo("원티드");
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidJobPostProvider.class)
    @DisplayName("JobPost 저장 실패: 빈 필드")
    void save_WhenEmptyField_ShouldThrowConstraintViolationException(JobPost.JobPostBuilder invalidJobPost) {
        assertThatThrownBy(() -> jobPostRepository.save(invalidJobPost.company(company).build()))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("JobPost 저장 실패: 의존관계 형성 실패(Company 없음)")
    void save_WhenCompanyNotFound_ShouldThrowConstraintViolationException() {
        JobPost invalidJobPost = JobPost.builder()
                .position("신입 백엔드 개발자")
                .reward(500000)
                .techStack("Java/Spring")
                .content("공고 상세 내용")
                .company(null)
                .build();

        assertThatThrownBy(() -> jobPostRepository.save(invalidJobPost))
                .isInstanceOf(ConstraintViolationException.class);
    }


    @Test
    @DisplayName("JobPost 단일 조회 테스트")
    void findById_WhenValid_ShouldReturnJobPost() {
        entityManager.persist(jobPost);

        Optional<JobPost> foundJobPost = jobPostRepository.findById(jobPost.getId());

        assertThat(foundJobPost).isPresent();
        assertThat(foundJobPost.get().getPosition()).isEqualTo("신입 백엔드 개발자");
    }

    @Test
    @DisplayName("JobPost 단일 조회 테스트: 실패")
    void findById_WhenNotFound_ShouldReturnJobPost() {
        Optional<JobPost> foundJobPost = jobPostRepository.findById(1328659832L);

        assertThat(foundJobPost).isEmpty();
    }

    @Test
    @DisplayName("JobPost 삭제 테스트")
    void testDeleteById() {
        entityManager.persist(jobPost);

        jobPostRepository.deleteById(jobPost.getId());

        Optional<JobPost> deletedJobPost = jobPostRepository.findById(jobPost.getId());
        assertThat(deletedJobPost).isEmpty();
    }

    @Test
    @DisplayName("JobPost 전체 조회 테스트")
    void findAll_WhenValid_ShouldReturnJobPostList() {
        Company naver = new Company("네이버", "한국", "판교");
        entityManager.persist(naver);

        JobPost jobPostByNaver = JobPost.builder()
                .position("신입 프론트엔드 개발자")
                .reward(30 * 100000)
                .techStack("React")
                .content("채용 공고 상세 내용")
                .company(naver)
                .build();

        entityManager.persist(jobPost);
        entityManager.persist(jobPostByNaver);

        List<JobPost> jobPosts = jobPostRepository.findAll();

        assertThat(jobPosts).hasSize(2);
        assertThat(jobPosts).extracting(JobPost::getPosition).containsExactlyInAnyOrder("신입 프론트엔드 개발자", "신입 백엔드 개발자");
    }

    private void jobPostSetUpForSearch() {
        JobPost.JobPostBuilder base = JobPost.builder().reward(30 * 100000);

        Company naver = entityManager.persist(new Company("네이버", "한국", "판교"));
        Company nintendo = entityManager.persist(new Company("닌텐도", "일본", "교토"));

        List<JobPost> jobPosts = List.of(
                base.position("신입 백엔드 개발자").techStack("Java").content("신입 개발자 모집").company(company).build(),
                base.position("경력 백엔드 개발자").techStack("TypeScript").content("경력 2년 이상").company(naver).build(),
                base.position("백엔드 개발자").techStack("Python/Django, Java/Spring").content("경력 우대").company(nintendo).build(),
                base.position("신입 프론트엔드 개발자").techStack("React").content("채용 공고 내용").company(company).build(),
                base.position("경력 프론트엔드 개발자").techStack("Angular").content("백엔드와 협업...중략").company(naver).build(),
                base.position("프론트엔드 개발자").techStack("Vue.js, React.js").content("채용 공고 내용").company(company).build()
        );

        jobPosts.stream().forEach(jobPost -> entityManager.persist(jobPost));
    }

    static Stream<Arguments> provideSearchQueryAndResultSize() {
        return Stream.of(
                // position 검색
                Arguments.of("백엔드", 4),
                Arguments.of("프론트엔드", 3),
                Arguments.of("신입", 2),
                Arguments.of("경력", 3),

                // tech stack 검색
                Arguments.of("Java", 2),
                Arguments.of("Python", 1),
                Arguments.of("React", 2),

                // 공고 상세 내용 검색
                Arguments.of("협업", 1),

                // 회사명 검색
                Arguments.of("원티드", 3),
                Arguments.of("네이버", 2),

                // 지역 검색
                Arguments.of("서울", 3),
                Arguments.of("판교", 2),

                // 국가 검색
                Arguments.of("한국", 5),
                Arguments.of("일본", 1)
        );
    }


    @ParameterizedTest
    @MethodSource("provideSearchQueryAndResultSize")
    @DisplayName("search 메서드 테스트")
    void search(String searchQuery, int size) {
        jobPostSetUpForSearch();
        List<JobPostResponse> searchResults = jobPostRepository.search(searchQuery);
        assertThat(searchResults).hasSize(size);
    }

    @Test
    @DisplayName("getOtherJobPost 메서드 테스트")
    void testGetOtherJobPost() {
        JobPost wantedJobPost = JobPost.builder()
                .position("신입 프론트엔드 개발자")
                .reward(50 * 100000)
                .techStack("React")
                .content("공고 상세 내용")
                .company(company)
                .build();

        // 포함되면 안되는 데이터(==네이버 채용공고) 모킹
        Company naver = entityManager.persist(new Company("네이버", "한국", "판교"));
        JobPost naverJobPost = JobPost.builder()
                .position("신입 프론트엔드 개발자")
                .reward(50 * 100000)
                .techStack("React")
                .content("공고 상세 내용")
                .company(naver)
                .build();

        entityManager.persist(jobPost); // 원티드
        entityManager.persist(wantedJobPost); // 원티드
        entityManager.persist(naverJobPost); // 네이버

        List<Long> otherJobPostIds = jobPostRepository.getOtherJobPost(company.getId(), jobPost.getId());

        assertThat(otherJobPostIds).hasSize(1);
        assertThat(otherJobPostIds.get(0)).isEqualTo(wantedJobPost.getId());
    }
}
