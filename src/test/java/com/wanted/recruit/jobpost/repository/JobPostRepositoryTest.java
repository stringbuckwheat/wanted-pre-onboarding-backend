package com.wanted.recruit.jobpost.repository;

import com.wanted.recruit.common.config.JpaConfig;
import com.wanted.recruit.common.config.QueryDslConfig;
import com.wanted.recruit.jobpost.entity.JobPost;
import com.wanted.recruit.company.entity.Company;
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
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QueryDslConfig.class, JpaConfig.class})
@DisplayName("JobPostRepository 테스트")
class JobPostRepositoryTest {

    @Autowired
    private JobPostRepository jobPostRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Company company;
    private JobPost wantedJobPost;

    private static final String TEST_PREFIX = UUID.randomUUID().toString() + "_";

    /**
     * company -> persistence
     * jobPost -> transient
     */
    @BeforeEach
    void setUp() {
        company = Company.builder().name("원티드").nation("한국").region("서울").build();
        entityManager.persist(company);

        wantedJobPost = JobPost.builder()
                .position("신입 백엔드 개발자")
                .reward(500000)
                .techStack("Java/Spring")
                .content("공고 상세 내용")
                .company(company)
                .build();

        JobPostTestHelper.setUpJobPostTestData(TEST_PREFIX, entityManager);
    }


    @Test
    @DisplayName("JobPost 저장: 성공 시나리오")
    void save_whenValid_ShouldSaveJobPost() {
        JobPost savedJobPost = jobPostRepository.save(wantedJobPost);

        assertThat(savedJobPost.getId()).isNotNull(); // PK 생성 여부 확인
        assertThat(savedJobPost.getCompany().getName()).isEqualTo("원티드"); // 필드 확인
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidJobPostProvider.class)
    @DisplayName("JobPost 저장 실패: 제약조건을 준수하지 못한 필드")
    void save_WhenEmptyField_ShouldThrowConstraintViolationException(JobPost.JobPostBuilder invalidJobPostBuilder) {
        // 불충분한 필드의 Builder를 영속화된 Company 엔티티와 함께 Build
        JobPost invalidJobPost = invalidJobPostBuilder.company(company).build();

        assertThatThrownBy(() -> jobPostRepository.save(invalidJobPost))
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
                .company(null) // 회사 정보 없음
                .build();

        assertThatThrownBy(() -> jobPostRepository.save(invalidJobPost))
                .isInstanceOf(ConstraintViolationException.class);
    }


    @Test
    @DisplayName("JobPost 단일 조회: 성공 시나리오")
    void findById_WhenValid_ShouldReturnJobPost() {
        entityManager.persist(wantedJobPost);

        // 방금 저장한 JobPost 엔티티를 조회
        Optional<JobPost> foundJobPost = jobPostRepository.findById(wantedJobPost.getId());

        assertThat(foundJobPost).isPresent(); // 존재 여부
        assertThat(foundJobPost.get().getPosition()).isEqualTo("신입 백엔드 개발자"); // 필드 확인
    }

    @Test
    @DisplayName("JobPost 단일 조회: 해당 엔티티 없음")
    void findById_WhenNotFound_ShouldReturnJobPost() {
        Optional<JobPost> foundJobPost = jobPostRepository.findById(1328659832L);
        assertThat(foundJobPost).isEmpty();
    }

    @Test
    @DisplayName("JobPost 삭제")
    void testDeleteById() {
        entityManager.persist(wantedJobPost);

        jobPostRepository.deleteById(wantedJobPost.getId());

        Optional<JobPost> deletedJobPost = jobPostRepository.findById(wantedJobPost.getId());
        assertThat(deletedJobPost).isEmpty();
    }

    @Test
    @DisplayName("JobPost 전체 조회: 성공 시나리오")
    void findAll_WhenValid_ShouldReturnJobPostList() {
        int prevSize = jobPostRepository.findAll().size();

        Company naver = new Company("네이버", "한국", "판교");
        entityManager.persist(naver);

        JobPost jobPostByNaver = JobPost.builder()
                .position("신입 프론트엔드 개발자")
                .reward(30 * 100000)
                .techStack("React")
                .content("채용 공고 상세 내용")
                .company(naver)
                .build();

        entityManager.persist(wantedJobPost); // 원티드 공고 저장
        entityManager.persist(jobPostByNaver); // 네이버 공고 저장

        List<JobPost> jobPosts = jobPostRepository.findAll();

        assertThat(jobPosts).hasSize(prevSize + 2);
    }

    /**
     * 검색 테스트를 위한 테스트 Argument 제공 메소드
     *
     * @return 검색어, 예상 결과의 개수
     */
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
    @DisplayName("JobPost 검색")
    void search(String searchQuery, int size) {
        List<JobPostResponse> searchResults = jobPostRepository.search(TEST_PREFIX + searchQuery);
        assertThat(searchResults).hasSize(size);
    }

    @Test
    @DisplayName("회사가 올린 다른 채용공고")
    void testGetOtherJobPost() {
        JobPost wantedJobPost2 = JobPost.builder()
                .position("신입 프론트엔드 개발자")
                .techStack("React")
                .content("공고 상세 내용")
                .company(company)
                .build();

        JobPost wantedJobPost3 = JobPost.builder()
                .position("경력 백엔드 개발자")
                .techStack("Spring")
                .content("공고 상세 내용")
                .company(company)
                .build();

        // 포함되면 안되는 데이터(==네이버 채용공고) 모킹
        Company naver = entityManager.persist(new Company("네이버", "한국", "판교"));
        JobPost naverJobPost = JobPost.builder()
                .position("신입 프론트엔드 개발자")
                .techStack("React")
                .content("공고 상세 내용")
                .company(naver)
                .build();

        entityManager.persist(wantedJobPost); // 원티드 -> 현재 조회한 공고
        entityManager.persist(wantedJobPost2); // 원티드 -> 회사가올린다른채용공고에 포함되어야 함
        entityManager.persist(wantedJobPost3); // 원티드 -> 회사가올린다른채용공고에 포함되어야 함
        entityManager.persist(naverJobPost); // 네이버 -> 포함X

        List<Long> otherJobPostIds = jobPostRepository.getOtherJobPost(company.getId(), wantedJobPost.getId());

        assertThat(otherJobPostIds).hasSize(2);

        // 포함 되어야 할 데이터들
        assertThat(otherJobPostIds).contains(wantedJobPost2.getId());
        assertThat(otherJobPostIds).contains(wantedJobPost3.getId());

        // 포함되지 말아야할 데이터
        assertThat(otherJobPostIds).doesNotContain(naverJobPost.getId());
        assertThat(otherJobPostIds).doesNotContain(wantedJobPost.getId());
    }
}
